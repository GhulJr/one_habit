package com.ghuljr.onehabit_data.cache.memory

import arrow.core.Either
import com.ghuljr.onehabit_tools.base.network.LoggedInUserService
import com.ghuljr.onehabit_error.BaseEvent
import com.ghuljr.onehabit_error.LoggedOutError
import com.ghuljr.onehabit_tools.di.ComputationScheduler
import com.ghuljr.onehabit_tools.extension.switchMapSingleRight
import com.ghuljr.onehabit_tools.extension.toEither
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import javax.inject.Inject

class MemoryCache<K, V>(
    private val loggedInUserService: LoggedInUserService,
    private val computationScheduler: Scheduler,
    private val provider: (ClassKey<K>) -> V
) {

    private val singleThreadScheduler = Schedulers.from(Executors.newSingleThreadExecutor())

    private val cache = ConcurrentHashMap<ClassKey<K>, V>()

    operator fun get(customKey: K? = null): Flowable<Either<BaseEvent, V>> =
        loggedInUserService.userIdFlowable
            .toEither { LoggedOutError as BaseEvent }
            .switchMapSingleRight { userId ->
                Single.fromCallable { this.cache }
                    .subscribeOn(singleThreadScheduler)
                    .map { cache ->
                        val userKey = ClassKey(userId, customKey)
                        cache.getOrPut(userKey) { provider(userKey) }
                    }
            }
            .observeOn(computationScheduler)


    class Provider<K, V> @Inject constructor(
        private val loggedInUserService: LoggedInUserService,
        @ComputationScheduler private val computationScheduler: Scheduler
    ) {
        fun create(provider: (ClassKey<K>) -> V): MemoryCache<K, V> =
            MemoryCache(loggedInUserService, computationScheduler, provider)
    }
}

data class ClassKey<K>(val userId: String, val customKey: K? = null)

