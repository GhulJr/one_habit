package com.ghuljr.onehabit_presenter.create_habit

import com.ghuljr.onehabit_presenter.base.BaseView

interface CreateHabitView : BaseView<CreateHabitPresenter> {

    fun handleCurrentStep(currentStep: CreateHabitPresenter.Step)
}