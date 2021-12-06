package com.ghuljr.onehabit_data

import arrow.core.Either
import arrow.core.Option
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

//TODO: Write tests for this!!!
abstract class DataSource<V>(
    protected val computationScheduler: ComputationScheduler,
    protected val networkScheduler: Scheduler,
    protected val refreshInterval: Long = 5L,
    protected val refreshIntervalUnit: TimeUnit = TimeUnit.MINUTES,
    protected val cachedDataFlowable: Flowable<CacheWithTime<V>>,
    protected val fetch: () -> Single<Either<BaseEvent, V>>,
    protected val invalidateAndUpdate: (CacheWithTime<V>) -> Unit
) {
    private val singleThreadScheduler = Schedulers.from(Executors.newSingleThreadExecutor())

    private val refreshProcessor = PublishProcessor.create<Unit>()

    // TODO: reduce source calls when subscribing
    val dataFlowable: Flowable<Either<BaseEvent, V>> = cachedDataFlowable
        .compose { flowable ->
            flowable.switchMap { cacheWithTime ->
                val dueToInMillis = cacheWithTime.value.fold({ 0L }, { cacheWithTime.dueToInMillis })
                Flowable.ambArray(
                    refreshProcessor,
                    Flowable.interval(
                        computeDelay(computationScheduler, dueToInMillis),
                        TimeUnit.MILLISECONDS,
                        computationScheduler
                    )
                        .toUnit()
                )
                    .subscribeOn(computationScheduler)
                    .switchMapSingle {
                        fetch()
                            .subscribeOn(networkScheduler)
                            .flatMapRightWithEither {
                                updateSingle(it.toOption(), computationScheduler, refreshInterval, refreshIntervalUnit)
                                    .map { it.toRight(NoDataError as BaseEvent) }
                            }
                    }
                    .startWithItem(cacheWithTime.value.toRight(NoDataError))
            }
        }
        .subscribeOn(computationScheduler)
        .distinctUntilChanged()
        .replay(1).refCount()

    init {
        require(refreshInterval > 0L)
    }

    fun refresh(): Single<Unit> = Single.fromCallable { refreshProcessor.onNext(Unit) }
        .subscribeOn(singleThreadScheduler)
        .observeOn(computationScheduler)

    private fun updateSingle(valueOption: Option<V>, scheduler: Scheduler, refreshInterval: Long, refreshIntervalUnit: TimeUnit): Single<Option<V>> = Single.fromCallable {
        invalidateAndUpdate(CacheWithTime(valueOption, scheduler.now(TimeUnit.MILLISECONDS) + refreshIntervalUnit.toMillis(refreshInterval)))
    }
        .map { valueOption }
        .subscribeOn(singleThreadScheduler)
        .observeOn(computationScheduler)

    private fun computeDelay(scheduler: Scheduler, timoeut: Long, ) =
        maxOf(timoeut - Math.abs(scheduler.now(TimeUnit.MILLISECONDS)), 0)

    data class CacheWithTime<V>(val value: Option<V>, val dueToInMillis: Long)
}


/**
 * This class should be in charge of:
 * - fetching data from network/storage and merging it,
 * - holding the data that is stored in the memory, so it would not call network/storage everytime it is needed.
 *
 * So what I need to do is to store memory cache as a state representation (as a model) with the information if the data is valid (expired time).
 * Data should be fetched when the cache is expired, client request a sync or (if it's possible) the data from network/storage has been changed.
 *
 * */