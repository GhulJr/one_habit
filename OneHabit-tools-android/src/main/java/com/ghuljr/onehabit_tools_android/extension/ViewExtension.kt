package com.ghuljr.onehabit_tools_android.extension

import android.content.Intent
import android.view.View
import android.widget.EditText
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import com.ghuljr.onehabit_tools.extension.toUnit
import com.jakewharton.rxbinding4.appcompat.navigationClicks
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
fun View.throttleClicks(timeout: Long = 500L, timeUnit: TimeUnit = TimeUnit.MILLISECONDS): Observable<Unit> = clicks().throttleFirst(timeout, timeUnit).share()
fun Toolbar.throttleNavigationClicks(timeout: Long = 200L, timeUnit: TimeUnit = TimeUnit.MILLISECONDS): Observable<Unit> = navigationClicks().throttleFirst(timeout, timeUnit).share()


fun View.focusLostObservable(): Observable<Unit> = focusChanges()
    .scan(false to false) { (_, previous), current -> previous to current }
    .filter { (previous, current) -> previous && !current }
    .toUnit()
    .share()

fun EditText.debouncedTextChanges(timeout: Long = 200L, timeUnit: TimeUnit = TimeUnit.MILLISECONDS): Observable<String> = textChanges().debounce(timeout, timeUnit).map { it.toString() }.share()

fun<V> NavController.onDestinationChangedObservable(onChange: (NavDestination) -> V): Observable<V> = Observable.create { emitter ->
    this.addOnDestinationChangedListener { _, destination, _ ->
        if(!emitter.isDisposed) emitter.onNext(onChange(destination))
    }
}

