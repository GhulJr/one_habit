package com.ghuljr.onehabit_presenter.generate_milestone.summary

import arrow.core.Option
import com.ghuljr.onehabit_error.BaseEvent
import com.ghuljr.onehabit_presenter.base.BaseView

interface SummarisePreviousMilestoneView: BaseView<SummarisePreviousMilestonePresenter> {

    fun handleEvent(event: Option<BaseEvent>)

    fun finishHabit()
    fun generateNewMilestone()

}