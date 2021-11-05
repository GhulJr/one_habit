package com.ghuljr.onehabit_data.cache.memory

import arrow.core.Either
import arrow.core.Option
import arrow.core.none
import arrow.core.toOption
import com.ghuljr.onehabit_error.BaseEvent
import com.ghuljr.onehabit_error.NoDataEvent
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

//TODO: create network/storage synchroniser, as well as base state representation (expiration time!)
abstract class CacheHolder<K, V>(
    protected val key: ClassKey<K>,
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

    //TODO: clear the data, if its not updated on time
    //TODO: set initial update time based on last saved time and current time?
    val stateCacheFlowable: Flowable<Either<BaseEvent, V>> = stateProcessor
        .compose { flowable ->
            flowable.switchMap {
                Flowable.merge(
                    refreshProcessor,
                    Flowable.interval(refreshInterval, refreshIntervalUnit, computationScheduler).toUnit()
                )
                    .subscribeOn(computationScheduler)
                    .switchMapSingle {
                        fetchDataSingle()
                            .subscribeOn(networkScheduler)
                            .flatMapRightWithEither {
                                updateSingle(it.toOption())
                                    .map { it.toRight(NoDataEvent as BaseEvent) }
                                    .observeOn(computationScheduler)
                            }
                    }
                    .startWithItem(it.value.toRight(NoDataEvent))
            }
        }
        .subscribeOn(computationScheduler)
        .distinctUntilChanged()
        .replay(1).refCount()

    abstract fun fetchDataSingle(): Single<Either<BaseEvent, V>>

    private fun updateSingle(valueOption: Option<V>): Single<Option<V>> = Single.fromCallable {
        stateProcessor.onNext(
            CacheWithTime(valueOption, singleThreadScheduler.now(TimeUnit.MILLISECONDS))
        )
    }
        .map { valueOption }
        .subscribeOn(singleThreadScheduler)

    private fun computeDelay(scheduler: Scheduler, downloadedTime: Long, timoeut: Long) =
        maxOf(timoeut - Math.abs(scheduler.now(TimeUnit.MILLISECONDS) - downloadedTime), 0)

    /*  private val stateProcessor by lazy { BehaviorProcessor.createDefault(initValueOption) }

      protected abstract val dataSourceFlowable: Flowable<Either<BaseEvent, V>>

      val cacheFlowable: Flowable<Either<BaseEvent, V>>> = Flowable.defer { dataSourceFlowable }
      .switchMapRightWithEither { cessor.toEither { NoDataEvent as BaseEvent } }
      .subscribeOn(computationScheduler)
      .replay(1)
      .refCount()

      //TODO: test carefully if this subscription won't change the parent's observable subscribe scheduler
      */

    private data class CacheWithTime<V>(val value: Option<V>, val timeInMillis: Long)
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