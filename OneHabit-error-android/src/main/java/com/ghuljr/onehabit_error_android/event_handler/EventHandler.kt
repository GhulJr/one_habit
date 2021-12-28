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

/** Maintain the order of classes, because error would be handled only by the first matching error handler. */
class EventHandler(
    private val eventManagers: List<BaseEventManager>,
    private val loadingView: View? = null
) {
    private val loadingEventManager: LoadingEventManager? = loadingView?.let { LoadingEventManager(it) }

    //TODO: make sure only one error handler can handles error
    operator fun invoke(event: Option<BaseEvent>) {
        var eventOptionToAttach = handleEventOrReturn(event, loadingEventManager)

        eventManagers.forEach {
            eventOptionToAttach = handleEventOrReturn(eventOptionToAttach, it)
        }
    }

    private fun handleEventOrReturn(event: Option<BaseEvent>, eventManager: BaseEventManager?): Option<BaseEvent> = if(eventManager?.handleEvent(event) == true) none() else event
}