package com.ghuljr.onehabit_data.cache.memory

import arrow.core.Either
import com.ghuljr.onehabit_data.cache.storage.TokenManager
import com.ghuljr.onehabit_error.BaseEvent
import com.ghuljr.onehabit_error.LoggedOutEvent
import com.ghuljr.onehabit_tools.extension.switchMapSingleRight
import com.ghuljr.onehabit_tools.extension.toEither
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.internal.schedulers.ComputationScheduler
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors

class MemoryCache<K, V>(private val tokenManager: TokenManager,
                        private val computationScheduler: ComputationScheduler,
                        private val provider: (ClassKey<K>) -> V) {

    private val singleThreadScheduler = Schedulers.from(Executors.newSingleThreadExecutor())

    private val cache = ConcurrentHashMap<ClassKey<K>, V>()

    operator fun get(customKey: K? = null): Flowable<Either<BaseEvent, V>> = tokenManager.userIdFlowable
        .toEither { LoggedOutEvent as BaseEvent }
        .switchMapSingleRight { userId ->
            Single.fromCallable { this.cache }
                .subscribeOn(singleThreadScheduler)
                .map { cache ->
                    val userKey = ClassKey(userId, customKey)
                    cache.getOrPut(userKey) { provider(userKey) }
                }
        }
        .observeOn(computationScheduler)

    inner class Provider(private val tokenManager: TokenManager, private val computationScheduler: ComputationScheduler) {
        fun create(provider: (ClassKey<K>) -> V): MemoryCache<K, V> = MemoryCache(tokenManager, computationScheduler, provider)
    }
}

data class ClassKey<K>(val userId: String, val customKey: K? = null)

