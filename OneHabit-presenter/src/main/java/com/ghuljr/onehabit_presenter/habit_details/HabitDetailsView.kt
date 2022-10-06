package com.ghuljr.onehabit_presenter.habit_details

import arrow.core.Option
import com.ghuljr.onehabit_error.BaseEvent
import com.ghuljr.onehabit_presenter.base.BaseView
import com.ghuljr.onehabit_tools.model.HabitTopic

interface HabitDetailsView : BaseView<HabitDetailsPresenter> {
    fun displayCurrentHabitData(habitTopic: HabitTopic, habitSubject: String, intensityProgress: Int)
    fun handleEvent(event: Option<BaseEvent>)
    fun displayMilestoneItems(items: List<MilestoneItem>)
    fun close()

    fun openMilestoneDetails(milestoneId: String, orderNumber: Int)
    fun displaySetAsCurrent(display: Boolean)
}