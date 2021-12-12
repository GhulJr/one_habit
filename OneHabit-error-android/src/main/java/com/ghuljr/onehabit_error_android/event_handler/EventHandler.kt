package com.ghuljr.onehabit_error_android.event_handler

import com.ghuljr.onehabit_error.BaseEvent
import com.ghuljr.onehabit_error_android.event_manager.BaseEventManager

class EventHandler<EVENT: BaseEvent>(private val eventManagers: List<BaseEventManager<EVENT>>) {

    //TODO: make sure only one error handler can handles error
    operator fun invoke(event: EVENT?) {
        eventManagers.forEach {
            it.handleEvent(event)
        }
    }
}