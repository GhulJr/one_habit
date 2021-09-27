package com.ghuljr.onehabit_data.cache.memory

import arrow.core.Either
import com.ghuljr.onehabit_data.cache.storage.TokenManager
import com.ghuljr.onehabit_error.BaseError
import com.ghuljr.onehabit_error.LoggedOutError
import com.ghuljr.onehabit_tools.extension.switchMapSingleRight
import com.ghuljr.onehabit_tools.extension.toEither
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.internal.schedulers.ComputationScheduler
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors

class ClassCacheHolder<K, V>(private val tokenManager: TokenManager,
                             private val computationScheduler: ComputationScheduler,
                             private val provider: (ClassCacheKey<K>) -> V) {

    private val singleThreadScheduler = Schedulers.from(Executors.newSingleThreadExecutor())

    private val cache = ConcurrentHashMap<ClassCacheKey<K>, V>()

    operator fun get(customKey: K? = null): Flowable<Either<BaseError, V>> = tokenManager.userIdFlowable
        .toEither { LoggedOutError as BaseError }
        .switchMapSingleRight { userId ->
            Single.fromCallable { this.cache }
                .subscribeOn(singleThreadScheduler)
                .map { cache ->
                    val userKey = ClassCacheKey(userId, customKey)
                    cache.getOrPut(userKey) { provider(userKey) }
                }
        }
        .observeOn(computationScheduler)
}

data class ClassCacheKey<K>(val userId: String, val customKey: K? = null)
