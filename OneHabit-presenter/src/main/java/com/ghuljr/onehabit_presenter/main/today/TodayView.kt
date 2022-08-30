package com.ghuljr.onehabit_presenter.main.today

import com.ghuljr.onehabit_presenter.base.BaseView

interface TodayView : BaseView<TodayPresenter> {
    fun openDetails()
    fun addCustomAction()
    fun submitItems(items: List<TodayItem>)
}