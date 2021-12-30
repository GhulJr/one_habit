package com.ghuljr.onehabit_tools.extension

import com.ghuljr.onehabit_tools.extension.TimeConstants.getCurrentTimeInSeconds
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import java.util.*
import java.util.concurrent.TimeUnit

/* Time */

object TimeConstants {
    val MINUTE_IN_SECONDS: Long = TimeUnit.MINUTES.toSeconds(1L)
    val HOUR_IN_SECONDS: Long = TimeUnit.HOURS.toSeconds(1L)

    fun getCurrentTimeInSeconds(timeZone: TimeZone = Calendar.getInstance().timeZone): Long = Calendar.getInstance(timeZone).timeInMillis / 1000
}


fun Long.getRemainingTimeInSeconds(timeZone: TimeZone = Calendar.getInstance().timeZone): Long = this - getCurrentTimeInSeconds(timeZone)

fun Long.isTimeReached(): Boolean = this - getCurrentTimeInSeconds() < 0

fun <T> Observable<T>.toCountdownTimer(startingValue: Long, scheduler: Scheduler): Observable<Long> = this.switchMap {
    if (startingValue < 0) Observable.just(0)
    else Observable.interval(1, TimeUnit.SECONDS, scheduler)
        .take(startingValue + 1)
        .map { (startingValue - it) }
        .startWithItem(startingValue)
}

fun Observable<Long>.toCountdownTimer(scheduler: Scheduler): Observable<Long> = this.switchMap { startingValue ->
    if (startingValue < 0) Observable.just(0)
    else Observable.interval(1, TimeUnit.SECONDS, scheduler)
        .take(startingValue + 1)
        .map { (startingValue - it) }
        .startWithItem(startingValue)
}
