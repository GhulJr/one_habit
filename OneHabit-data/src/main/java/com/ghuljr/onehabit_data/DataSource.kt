package com.ghuljr.onehabit_data

import arrow.core.Either
import arrow.core.Option
import arrow.core.right
import arrow.core.toOption
import com.ghuljr.onehabit_error.BaseEvent
import com.ghuljr.onehabit_error.NoDataError
import com.ghuljr.onehabit_tools.extension.*
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.internal.schedulers.ComputationScheduler
import io.reactivex.rxjava3.processors.PublishProcessor
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class DataSource<V>(
    protected val computationScheduler: Scheduler,
    protected val networkScheduler: Scheduler,
    private val singleThreadScheduler: Scheduler = Schedulers.from(Executors.newSingleThreadExecutor()),
    protected val refreshInterval: Long = 5L,
    protected val refreshIntervalUnit: TimeUnit = TimeUnit.MINUTES,
    protected val cachedDataFlowable: Flowable<CacheWithTime<V>>,
    protected val fetch: () -> Single<Either<BaseEvent, V>>,
    protected val invalidateAndUpdate: (CacheWithTime<V>) -> Unit
) {

    private val refreshProcessor = PublishProcessor.create<Unit>()

    // TODO: reduce source calls when subscribing
    // TODO: cache time, when the data should be treated as invalid
    val dataFlowable: Flowable<Either<BaseEvent, V>> = cachedDataFlowable
        .compose { flowable ->
            flowable.switchMap { cacheWithTime ->
                Flowable.merge(
                    refreshProcessor,
                    Flowable.interval(
                        computeDelay(computationScheduler, cacheWithTime.dueToInMillis),
                        TimeUnit.MILLISECONDS,
                        computationScheduler
                    )
                        .take(1)
                        .toUnit()
                )
                    .debounce(500L, TimeUnit.MILLISECONDS, computationScheduler)
                    .switchMapSingle {
                        fetch()
                            .subscribeOn(networkScheduler)
                            .flatMapRightWithEither {
                                updateSingle(it.toOption())
                                    .map { it.toRight(NoDataError as BaseEvent) }
                            }
                    }
                    .let {
                        if (cacheWithTime.value.isDefined())
                            it.startWithItem(cacheWithTime.value.orNull()!!.right())
                        else it
                    }
            }
        }
        .subscribeOn(computationScheduler)
        .distinctUntilChanged()
        .replay(1).refCount()

    init {
        require(refreshInterval >= 0L)
    }

    fun refresh(): Single<Unit> = Single.fromCallable { refreshProcessor.onNext(Unit) }
        .subscribeOn(singleThreadScheduler)
        .observeOn(computationScheduler)

    private fun updateSingle(
        valueOption: Option<V>,

    ): Single<Option<V>> = Single.fromCallable {
        invalidateAndUpdate(
            CacheWithTime(
                valueOption,
                computationScheduler.now(TimeUnit.MILLISECONDS) + refreshIntervalUnit.toMillis(refreshInterval)
            )
        )
    }
        .map { valueOption }
        .subscribeOn(singleThreadScheduler)
        .observeOn(computationScheduler)

    private fun computeDelay(scheduler: Scheduler, timoeut: Long) =
        maxOf(timoeut - Math.abs(scheduler.now(TimeUnit.MILLISECONDS)), 0)

    data class CacheWithTime<V>(val value: Option<V>, val dueToInMillis: Long)
}