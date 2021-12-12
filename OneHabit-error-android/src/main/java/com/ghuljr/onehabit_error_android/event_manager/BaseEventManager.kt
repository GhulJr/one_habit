package com.ghuljr.onehabit_error_android.event_manager

import android.view.View
import androidx.annotation.CallSuper
import androidx.core.view.isVisible
import com.ghuljr.onehabit_error.BaseEvent

abstract class BaseEventManager<EVENT: BaseEvent>(
    private val eventView: View,
    private val shouldHandleEvent: (EVENT) -> Boolean = { true }
) {

    @CallSuper
    open fun handleEvent(event: EVENT?) {
        eventView.isVisible = if(event != null) shouldHandleEvent(event) else false
    }

}