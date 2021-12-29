package com.ghuljr.onehabit_tools_android.extension

import android.content.Intent
import android.view.View
import android.widget.EditText
import arrow.core.Option
import com.ghuljr.onehabit_error.ValidationError
import com.ghuljr.onehabit_tools.extension.toUnit
import com.google.android.material.textfield.TextInputLayout
import com.jakewharton.rxbinding4.view.clicks
import com.jakewharton.rxbinding4.view.focusChanges
import com.jakewharton.rxbinding4.widget.textChanges
import io.reactivex.rxjava3.core.Observable
import java.util.concurrent.TimeUnit


// Intents
fun Intent.asSingleTop(): Intent = apply {
    flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
}

// Layout events
fun View.throttleClicks(timeout: Long = 200L, timeUnit: TimeUnit = TimeUnit.MILLISECONDS): Observable<Unit> = clicks().throttleFirst(timeout, timeUnit).share()

fun View.focusLostObservable(): Observable<Unit> = focusChanges()
    .scan(false to false) { (_, previous), current -> previous to current }
    .filter { (previous, current) -> previous && !current }
    .toUnit()
    .share()

fun EditText.debouncedTextChanges(timeout: Long = 200L, timeUnit: TimeUnit = TimeUnit.MILLISECONDS): Observable<String> = textChanges().debounce(timeout, timeUnit).map { it.toString() }.share()

