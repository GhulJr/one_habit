package com.ghuljr.onehabit_tools_android.extension

import android.content.Intent
import android.content.res.Resources
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.EditText
import androidx.annotation.AttrRes
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

fun <T> Resources.Theme.getFromTheme(@AttrRes res: Int, getValue: TypedArray.() -> T): T =  with(obtainStyledAttributes(intArrayOf(res))) { getValue(this@with).also { recycle() } }
fun <T> Resources.Theme.getFromAttr(@AttrRes res: Int, attrs: AttributeSet?, getValue: TypedArray.() -> T): T =  with(obtainStyledAttributes(attrs, intArrayOf(res), 0, 0)) { getValue(this@with).also { recycle() } }

fun  Resources.Theme.getColorFromTheme(@AttrRes res: Int): Int = getFromTheme(res) { getColor(0, 0) }
fun  Resources.Theme.getIntFromTheme(@AttrRes res: Int, defValue: Int): Int = getFromTheme(res) { getInt(0, defValue) }
fun  Resources.Theme.getResIdFromTheme(@AttrRes res: Int): Int = getFromTheme(res) { getResourceId(0, 0) }
fun  Resources.Theme.getIntFromAttr(@AttrRes res: Int, defValue: Int, attrs: AttributeSet?): Int = getFromAttr(res, attrs) { getInt(0, defValue) }
fun  Resources.Theme.getColorFromAttr(@AttrRes res: Int, attrs: AttributeSet?): Int = getFromAttr(res, attrs) { getColor(0, 0) }
fun  Resources.Theme.getStringFromAttr(@AttrRes res: Int, attrs: AttributeSet?): String? = getFromAttr(res, attrs) { getString(0) }
fun  Resources.Theme.getIntFromAttr(@AttrRes res: Int, attrs: AttributeSet?): Int = getFromAttr(res, attrs) { getInt(0, 0) }
fun  Resources.Theme.getDrawableFromAttr(@AttrRes res: Int, attrs: AttributeSet?): Drawable? = getFromAttr(res, attrs) { getDrawable(0) }
