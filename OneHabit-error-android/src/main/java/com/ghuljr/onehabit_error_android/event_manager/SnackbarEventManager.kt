package com.ghuljr.onehabit_error_android.event_manager

import android.view.View
import androidx.core.content.ContextCompat
import arrow.core.Option
import com.ghuljr.onehabit_error.BaseEvent
import com.ghuljr.onehabit_error_android.R
import com.ghuljr.onehabit_error_android.base.BaseEventManager
import com.ghuljr.onehabit_error_android.extension.textForEvent
import com.google.android.material.snackbar.Snackbar

class SnackbarEventManager(
    eventView: View,
    private val anchorView: View? = null,
    private val duration: Int = Snackbar.LENGTH_LONG,
    private val actionWithName: Pair<() -> Unit, String>? = null,
    private val shouldHandleEvent: (BaseEvent) -> Boolean = { true }
) : BaseEventManager(eventView) {

    override fun handleEvent(event: Option<BaseEvent>): Boolean {
        event.map {
            if (shouldHandleEvent(it)) {
                Snackbar.make(eventView, it.textForEvent(eventView.resources), duration)
                    .setAnchorView(anchorView)
                    .setBackgroundTint(ContextCompat.getColor(eventView.context, R.color.primary_black))
                    .setTextColor(ContextCompat.getColor(eventView.context, R.color.primary_white))
                    .let {
                        if (actionWithName != null) {
                            val (action, name) = actionWithName
                            it.setAction(name) { action() }
                        } else it
                    }
                    .show()
                return true
            }
        }
        return false
    }
}