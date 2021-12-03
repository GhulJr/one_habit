package com.ghuljr.onehabit_data.cache.memory

import arrow.core.Either
import arrow.core.Option
import arrow.core.none
import arrow.core.toOption
import com.ghuljr.onehabit_error.BaseEvent
import com.ghuljr.onehabit_error.NoDataError
import com.ghuljr.onehabit_tools.extension.*
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.internal.schedulers.ComputationScheduler
import io.reactivex.rxjava3.processors.BehaviorProcessor
import io.reactivex.rxjava3.processors.PublishProcessor
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

//TODO: Write tests for this!!!
abstract class CacheHolder<K, V>(
    protected val computationScheduler: ComputationScheduler,
    protected val networkScheduler: Scheduler,
    protected val initValueOption: Option<V> = none(),
    protected val refreshInterval: Long = 5L,
    protected val refreshIntervalUnit: TimeUnit = TimeUnit.MINUTES
) {
    private val singleThreadScheduler = Schedulers.from(Executors.newSingleThreadExecutor())

    private val refreshProcessor = PublishProcessor.create<Unit>()
    private val stateProcessor by lazy {
        BehaviorProcessor.createDefault(
            CacheWithTime(
                initValueOption,
                singleThreadScheduler.now(TimeUnit.MILLISECONDS)
            )
        )
    }

    val stateCacheFlowable: Flowable<Either<BaseEvent, V>> = stateProcessor
        .compose { flowable ->
            flowable.switchMap {
                Flowable.ambArray(
                    refreshProcessor,
                    Flowable.interval(
                        computeDelay(
                            computationScheduler,
                            it.dueToInMillis,
                        ),
                        TimeUnit.MILLISECONDS,
                        computationScheduler
                    )
                        .toUnit()
                )
                    .subscribeOn(computationScheduler)
                    .switchMapSingle {
                        fetchDataSingle()
                            .subscribeOn(networkScheduler)
                            .flatMapRightWithEither {
                                updateSingle(it.toOption())
                                    .map { it.toRight(NoDataError as BaseEvent) }
                                    .observeOn(computationScheduler)
                            }
                    }
                    .startWithItem(it.value.toRight(NoDataError))
            }
        }
        .subscribeOn(computationScheduler)
        .distinctUntilChanged()
        .replay(1).refCount()

    init {
        require(refreshInterval > 0L)
    }

    abstract fun fetchDataSingle(): Single<Either<BaseEvent, V>>

    private fun updateSingle(valueOption: Option<V>): Single<Option<V>> = Single.fromCallable {
        stateProcessor.onNext(
            CacheWithTime(valueOption, singleThreadScheduler.now(TimeUnit.MILLISECONDS) + refreshIntervalUnit.toMillis(refreshInterval))
        )
    }
        .map { valueOption }
        .subscribeOn(singleThreadScheduler)

    private fun computeDelay(scheduler: Scheduler, timoeut: Long) =
        maxOf(timoeut - Math.abs(scheduler.now(TimeUnit.MILLISECONDS)), 0)

    private data class CacheWithTime<V>(val value: Option<V>, val dueToInMillis: Long)
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