package com.ghuljr.onehabit_error_android.event_handler

import android.view.View
import androidx.core.view.isVisible
import arrow.core.Option
import arrow.core.none
import arrow.core.some
import arrow.core.toOption
import com.ghuljr.onehabit_error.BaseEvent
import com.ghuljr.onehabit_error.LoadingEvent
import com.ghuljr.onehabit_error_android.base.BaseEventManager
import com.ghuljr.onehabit_error_android.event_manager.LoadingEventManager

class EventHandler(
    private val eventManagers: List<BaseEventManager>,
    private val loadingView: View? = null
) {
    private val loadingEventManager: LoadingEventManager? = loadingView?.let { LoadingEventManager(it) }

    //TODO: make sure only one error handler can handles error
    operator fun invoke(event: Option<BaseEvent>) {
        loadingEventManager?.handleEvent(event)
        eventManagers.forEach {
            it.handleEvent(event)
        }
    }
}