package com.ghuljr.onehabit_data.cache.memory

import arrow.core.Either
import com.ghuljr.onehabit_data.repository.LoggedInUserRepository
import com.ghuljr.onehabit_error.BaseError
import com.ghuljr.onehabit_error.LoggedOutError
import com.ghuljr.onehabit_tools.di.ComputationScheduler
import com.ghuljr.onehabit_tools.extension.switchMapSingleRight
import com.ghuljr.onehabit_tools.extension.toEither
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors

/** MemoryCache is responsible for containing and switching different sets of data in memory
 * @param loggedInUserRepository    used to get current logged in user id
 * @param computationScheduler      used to schedule computation work on background thread
 * @param provider                  method, that will create new data, if nothing is cached
 * */
class MemoryCache<K, V> @AssistedInject constructor(
    private val loggedInUserRepository: LoggedInUserRepository,
    @ComputationScheduler private val computationScheduler: Scheduler,
    @Assisted private val provider: (ClassKey<K>) -> V
) {

    private val singleThreadScheduler = Schedulers.from(Executors.newSingleThreadExecutor())

    private val cache = ConcurrentHashMap<ClassKey<K>, V>()

    /** Used to obtain particular instance of code
     * @param customKey         optional parameter, that, alongside user id, is used to identify particular set of cached data
     * @return                  flowable, that will emmit either LoggedOutError, if there is no user, or particular set of data
     * */
    operator fun get(customKey: K? = null): Flowable<Either<BaseError, V>> =
        loggedInUserRepository.userIdFlowable
            .toEither { LoggedOutError as BaseError }
            .switchMapSingleRight { userId ->
                Single.fromCallable { this.cache }
                    .subscribeOn(singleThreadScheduler)
                    .map { cache ->
                        val userKey = ClassKey(userId, customKey)
                        cache.getOrPut(userKey) { provider(userKey) }
                    }
            }
            .observeOn(computationScheduler)

    @AssistedFactory
    interface Factory<K, V> {

        /** Used to inject required arguments and implement provider manually
         * @param provider      method, that will create new data, if nothing is cached
         * @return              new instance of MemoryCache
         **/
        fun create  (provider: (ClassKey<K>) -> V): MemoryCache<K, V>
    }
}

data class ClassKey<K>(val userId: String, val customKey: K? = null)

