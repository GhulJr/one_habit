package com.ghuljr.onehabit_tools_android.extension

import android.content.Intent
import android.view.View
import android.widget.EditText
import com.jakewharton.rxbinding4.view.clicks
import com.jakewharton.rxbinding4.widget.textChanges
import io.reactivex.rxjava3.core.Observable
import java.util.concurrent.TimeUnit


fun Intent.asSingleTop(): Intent = apply {
    flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
}

fun View.throttleClicks(): Observable<Unit> = clicks().throttleFirst(500L, TimeUnit.MILLISECONDS).share()
fun EditText.debouncedTextChanges(): Observable<String> = textChanges().debounce(500L, TimeUnit.MILLISECONDS).map { it.toString() }.share()