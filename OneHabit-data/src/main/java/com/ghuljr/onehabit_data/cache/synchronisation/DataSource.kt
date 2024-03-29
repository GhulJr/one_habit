package com.ghuljr.onehabit_data.cache.synchronisation

import arrow.core.Either
import arrow.core.Option
import arrow.core.right
import arrow.core.toOption
import com.ghuljr.onehabit_error.BaseError
import com.ghuljr.onehabit_error.LoggedOutError
import com.ghuljr.onehabit_error.NoDataError
import com.ghuljr.onehabit_tools.extension.*
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.processors.PublishProcessor
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.math.abs

/** DataSource is a bridge to store, fetch, obtain and refresh particular data in given storage system.
 * @param refreshInterval           time after data should be refreshed
 * @param refreshIntervalUnit       time unit of refresh
 * @param cachedDataFlowable        flowable with cached data
 * @param fetch                     function to fetch fresh data
 * @param invalidateAndUpdate       function to invalidate current data
 * @param computationScheduler      scheduler on which data is stored and read from storage
 * @param networkScheduler          scheduler on which fetch is called
 * @param singleThreadScheduler     scheduler used to assure concurrency safety while writing to storage
 **/
class DataSource<V>(
    private val refreshInterval: Long = 5L,
    private val refreshIntervalUnit: TimeUnit = TimeUnit.MINUTES,
    private val cachedDataFlowable: Flowable<CacheWithTime<V>>,
    private val fetch: () -> Single<Either<BaseError, V>>,
    private val invalidateAndUpdate: (CacheWithTime<V>) -> Unit,
    private val computationScheduler: Scheduler,
    private val networkScheduler: Scheduler,
    private val singleThreadScheduler: Scheduler = Schedulers.from(Executors.newSingleThreadExecutor())
) {

    init {
        require(refreshInterval >= 0L)
    }

    /** Flowable that emits current data, or error when data is not present or fetch failed **/
    val dataFlowable: Flowable<Either<BaseError, V>> = cachedDataFlowable
        .switchMap { cacheWithTime ->
            Flowable.interval(
                computeDelay(computationScheduler, cacheWithTime.dueToInMillis),
                TimeUnit.MILLISECONDS,
                computationScheduler
            )
                .take(1)
                .toUnit()
                .switchMapSingle { _fetch() }
                .let {
                    if (cacheWithTime.value.isDefined())
                        it.startWithItem(cacheWithTime.value.orNull()!!.right())
                    else it
                }
        }
        .subscribeOn(computationScheduler)
        .distinctUntilChanged()
        .replay(1).refCount()

    private fun _fetch() = fetch()
        .subscribeOn(networkScheduler)
        .observeOn(singleThreadScheduler)
        .flatMapRightWithEither {
            updateSingle(it.toOption())
                .map { it.toRight(NoDataError as BaseError) }
        }
        .observeOn(computationScheduler)

    /** Force refresh data
     * @return              single that indicates that refresh was scheduled
     **/
    fun refresh(): Maybe<Either<BaseError, V>> = _fetch()
        .toMaybe()
        .subscribeOn(singleThreadScheduler)
        .observeOn(computationScheduler)


    /** Update stored data
     * @param valueOption       option with value or empty if it should be cleared
     * @return                  single with updated value
     **/
    private fun updateSingle(valueOption: Option<V>): Single<Option<V>> = Single.fromCallable {
        invalidateAndUpdate(
            CacheWithTime(
                valueOption,
                singleThreadScheduler.now(TimeUnit.MILLISECONDS) + refreshIntervalUnit.toMillis(refreshInterval)
            )
        )
    }
        .map { valueOption }
        .subscribeOn(singleThreadScheduler)
        .observeOn(computationScheduler)

    private fun computeDelay(scheduler: Scheduler, timeout: Long) =
        maxOf(timeout - abs(scheduler.now(TimeUnit.MILLISECONDS)), 0)

    data class CacheWithTime<V>(val value: Option<V>, val dueToInMillis: Long)
}