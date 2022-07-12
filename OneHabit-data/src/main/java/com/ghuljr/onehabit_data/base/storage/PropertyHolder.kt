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

/** PropertyHolder is used to store small amount of data into preferences
 * @param preferences           class that represents preferences to store data
 * @param computationScheduler  used to schedule computation work on background thread
 * @param key                   identifier that is used to get and set particular value to preferences
 * @param defaultValue          value used, when there is nothing saved, Option.None by default
 **/
class PropertyHolder<TYPE> @AssistedInject constructor(
    private val preferences: Preferences,
    @ComputationScheduler private val computationScheduler: Scheduler,
    @Assisted private val key: String,
    @Assisted private val defaultValue: Option<TYPE> = none()
) {

    private val singleThreadScheduler = Schedulers.from(Executors.newSingleThreadExecutor())

    private val valueProcessor: BehaviorProcessor<Option<TYPE>> =
        BehaviorProcessor.createDefault(preferences.getValue(key, defaultValue))

    /** Get current value
     * @return          flowable that will emit value for given key, if it's stored
     **/
    fun get(): Flowable<Option<TYPE>> =
        valueProcessor
            .subscribeOn(computationScheduler)
            .replay(1)
            .refCount()

    /** Set value to preferences
     * @param value     new value to be set
     * @return          single, with information weather value was set successfully or failed
     **/
    //TODO: verify this, it might be error prone, making either with error might make it more readable
    fun set(value: Option<TYPE>): Single<Boolean> = Single.fromCallable {
        preferences.setValue(key, value)
            .also { if (it) valueProcessor.onNext(value) }
    }
        .subscribeOn(singleThreadScheduler)
        .observeOn(computationScheduler)

    @AssistedFactory
    interface Factory<TYPE> {
        /** Used to inject required arguments and implement key and default value
         * @param key                   identifier that is used to get and set particular value to preferences
         * @param defaultValue          value used, when there is nothing saved, Option.None by default
         * @return                      new instance of PropertyHolder
         **/
        fun create(key: String, defaultValue: Option<TYPE> = none()): PropertyHolder<TYPE>
    }
}