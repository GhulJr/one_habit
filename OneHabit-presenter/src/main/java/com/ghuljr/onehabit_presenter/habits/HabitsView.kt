package com.ghuljr.onehabit_presenter.habits

import arrow.core.Option
import com.ghuljr.onehabit_error.BaseEvent
import com.ghuljr.onehabit_presenter.base.BaseView

interface HabitsView : BaseView<HabitsPresenter> {

    fun submitList(habits: List<HabitItem>)
    fun handleEvent(event: Option<BaseEvent>)
    fun openHabitDetails(habitId: String)
}