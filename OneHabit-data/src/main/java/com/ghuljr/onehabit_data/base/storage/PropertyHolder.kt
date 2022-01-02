package com.ghuljr.onehabit_data.base.storage

import arrow.core.Option
import arrow.core.none
import com.ghuljr.onehabit_tools.base.storage.Preferences
import com.ghuljr.onehabit_tools.di.ComputationScheduler
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.processors.BehaviorProcessor
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.Executors

class PropertyHolder<TYPE> @AssistedInject constructor(
    private val preferences: Preferences,
    @ComputationScheduler private val computationScheduler: Scheduler,
    @Assisted private val key: String,
    @Assisted private val defaultValue: Option<TYPE> = none()
) {

    private val singleThreadScheduler = Schedulers.from(Executors.newSingleThreadExecutor())

    private val valueProcessor: BehaviorProcessor<Option<TYPE>> =
        BehaviorProcessor.createDefault(preferences.getValue(key, defaultValue))

    fun get(): Flowable<Option<TYPE>> =
        valueProcessor
            .subscribeOn(computationScheduler)
            .replay(1)
            .refCount()

    //TODO: verify this, it might be error prone, making either with error might make it more readable
    fun set(value: Option<TYPE>): Single<Boolean> = Single.fromCallable {
        preferences.setValue(key, value)
            .also { if (it) valueProcessor.onNext(value) }
    }
        .subscribeOn(singleThreadScheduler)
        .observeOn(computationScheduler)

    @AssistedFactory
    interface Factory<TYPE> {
        fun create(key: String, defaultValue: Option<TYPE> = none()): PropertyHolder<TYPE>
    }
}