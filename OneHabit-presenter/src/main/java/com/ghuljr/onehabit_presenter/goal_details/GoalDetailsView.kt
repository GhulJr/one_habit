package com.ghuljr.onehabit_presenter.goal_details

import com.ghuljr.onehabit_presenter.base.BaseView

interface GoalDetailsView : BaseView<GoalDetailsPresenter> {

    fun displayDayNumber(dayNumber: Int)
}