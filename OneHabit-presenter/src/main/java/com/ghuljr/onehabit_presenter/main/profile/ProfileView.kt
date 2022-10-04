package com.ghuljr.onehabit_presenter.main.profile

import arrow.core.Option
import com.ghuljr.onehabit_error.BaseEvent
import com.ghuljr.onehabit_presenter.base.BaseView
import com.ghuljr.onehabit_tools.model.HabitTopic

interface ProfileView : BaseView<ProfilePresenter> {
    fun displayCurrentHabitData(habitTopic: HabitTopic, habitSubject: String, intensityProgress: Int)
    fun openCurrentHabitDetails(habitId: String)
    fun handleEvent(event: Option<BaseEvent>)
}