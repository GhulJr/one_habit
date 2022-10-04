package com.ghuljr.onehabit_presenter.main.timeline

import arrow.core.Option
import com.ghuljr.onehabit_error.BaseEvent
import com.ghuljr.onehabit_presenter.base.BaseView

interface TimelineView : BaseView<TimelinePresenter> {

    fun submitItems(items: List<TimelineItem>)
    fun handleEvent(event: Option<BaseEvent>)
    fun openGoalDetails(goalId: String, orderNumber: Int)
}