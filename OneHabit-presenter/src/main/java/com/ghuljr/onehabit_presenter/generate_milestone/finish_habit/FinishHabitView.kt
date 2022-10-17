package com.ghuljr.onehabit_presenter.generate_milestone.finish_habit

import arrow.core.Option
import com.ghuljr.onehabit_error.BaseEvent
import com.ghuljr.onehabit_presenter.base.BaseView

interface FinishHabitView : BaseView<FinishHabitPresenter> {

    fun handleEvent(event: Option<BaseEvent>)
    fun finish()
}