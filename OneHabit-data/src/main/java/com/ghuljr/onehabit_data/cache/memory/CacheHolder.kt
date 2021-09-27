package com.ghuljr.onehabit_data.cache.memory

import arrow.core.Either
import arrow.core.Option
import arrow.core.none
import com.ghuljr.onehabit_error.BaseError
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.internal.schedulers.ComputationScheduler
import io.reactivex.rxjava3.processors.BehaviorProcessor
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.Executors

//TODO: create network/storage synchroniser, as well as base state representation (expiration time!)
abstract class CacheHolder<K, V>(
    protected val key: ClassKey<K>,
    protected val computationScheduler: ComputationScheduler,
    protected val initValueOption: Option<V> = none()
) {
    private val singleThreadScheduler = Schedulers.from(Executors.newSingleThreadExecutor())
    private val stateProcessor by lazy { BehaviorProcessor.createDefault(initValueOption) }

    val cacheFlowable: Flowable<Option<V>> = stateProcessor
        .subscribeOn(computationScheduler)
        .replay(1)
        .refCount()

    fun updateSingle(valueOption: Option<V>): Single<Unit> = Single.fromCallable { stateProcessor.onNext(valueOption) }
        .subscribeOn(singleThreadScheduler)
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