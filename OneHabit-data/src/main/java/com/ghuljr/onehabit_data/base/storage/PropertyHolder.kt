package com.ghuljr.onehabit_data.base.storage

import arrow.core.Option
import arrow.core.none
import com.ghuljr.onehabit_tools.base.storage.Preferences
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.internal.schedulers.ComputationScheduler
import io.reactivex.rxjava3.processors.BehaviorProcessor

class PropertyHolder<TYPE>(
    private val preferences: Preferences,
    private val computationScheduler: ComputationScheduler,
    private val key: String,
    private val defaultValue: Option<TYPE> = none()
) {

    private val valueProcessor: BehaviorProcessor<Option<TYPE>> =
        BehaviorProcessor.createDefault(preferences.getValue(key, defaultValue))

    fun get(): Flowable<Option<TYPE>> =
        valueProcessor
            .subscribeOn(computationScheduler)
            .replay(1)
            .refCount()

    //TODO: verify this, it might be error prone, making either with error might make it more readable
    fun set(value: Option<TYPE>): Boolean = preferences.setValue(key, value)
        .also { if(it) valueProcessor.onNext(value) }
}