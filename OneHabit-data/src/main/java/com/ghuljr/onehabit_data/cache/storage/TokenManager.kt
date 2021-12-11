package com.ghuljr.onehabit_data.cache.storage

import arrow.core.Option
import arrow.core.none
import com.ghuljr.onehabit_tools.base.storage.Preferences
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.internal.schedulers.ComputationScheduler
import io.reactivex.rxjava3.processors.BehaviorProcessor
import kotlin.reflect.KProperty

class TokenManager(preferences: Preferences, computationScheduler: ComputationScheduler) {

    private val tokenHolder = PropertyHolder<String>(preferences, computationScheduler, KEY_TOKEN)

    val isUserLoggedInFlowable: Flowable<Boolean> = tokenHolder.get()
        .map { it.isDefined() }
        .replay(1)
        .refCount()

    val userIdFlowable: Flowable<Option<String>> = tokenHolder.get()
        .replay(1)
        .refCount()

    fun setToken(tokenOption: Option<String>): Boolean = tokenHolder.set(tokenOption)

    companion object {
        private const val KEY_TOKEN = "key_token"
    }
}

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