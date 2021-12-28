package com.ghuljr.onehabit_error_android.event_manager

import android.view.View
import androidx.core.view.isVisible
import arrow.core.Option
import com.ghuljr.onehabit_error.LoadingEvent
import com.ghuljr.onehabit_error.BaseEvent
import com.ghuljr.onehabit_error_android.base.BaseEventManager

//TODO: think about making it a public class
class LoadingEventManager(loadingView: View): BaseEventManager(loadingView) {

    override fun handleEvent(event: Option<BaseEvent>): Boolean {
        eventView.isVisible = event.orNull() is LoadingEvent
        return eventView.isVisible
    }
}