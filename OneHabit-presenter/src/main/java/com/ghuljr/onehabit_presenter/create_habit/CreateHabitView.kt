package com.ghuljr.onehabit_presenter.create_habit

import arrow.core.Option
import com.ghuljr.onehabit_error.BaseEvent
import com.ghuljr.onehabit_presenter.base.BaseView
import com.ghuljr.onehabit_tools.model.HabitTopic

interface CreateHabitView : BaseView<CreateHabitPresenter> {

    fun handleCurrentStep(activeSteps: Set<CreateHabitPresenter.Step>)
    fun setAction(habitTopic: HabitTopic)
    fun setHabitFrequency(frequency: Int)

    fun handleEvent(event: Option<BaseEvent>)
    fun close()
}