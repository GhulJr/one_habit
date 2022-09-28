package com.ghuljr.onehabit_presenter.create_habit

import com.ghuljr.onehabit_presenter.base.BaseView
import com.ghuljr.onehabit_tools.model.HabitTopic

interface CreateHabitView : BaseView<CreateHabitPresenter> {

    fun handleCurrentStep(activeSteps: Set<CreateHabitPresenter.Step>)
    fun setAction(habitTopic: HabitTopic)
}