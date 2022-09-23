package com.ghuljr.onehabit_data.utils

import arrow.core.Option
import arrow.core.none
import com.ghuljr.onehabit_data.cache.synchronisation.DataSource
import com.ghuljr.onehabit_error.BaseError

object TestData {

    object TestError: BaseError

    fun <T> cacheWithTime(
        value: Option<T> = none(),
        dueInMillis: Long = 0L
    ): DataSource.CacheWithTime<T> =
        DataSource.CacheWithTime(
            value = value,
            dueToInMillis = dueInMillis
        )
}