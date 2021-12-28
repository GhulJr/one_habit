package com.ghuljr.onehabit_error_android.base

import android.view.View
import androidx.annotation.CallSuper
import androidx.core.view.isVisible
import arrow.core.Option
import com.ghuljr.onehabit_error.BaseEvent
import com.ghuljr.onehabit_error.LoadingEvent

abstract class BaseEventManager(protected val eventView: View) {

    abstract fun handleEvent(event: Option<BaseEvent>): Boolean
}