package com.ghuljr.onehabit_presenter.main.today

import com.ghuljr.onehabit_error.BaseError
import com.ghuljr.onehabit_presenter.base.BaseView

interface TodayView : BaseView<TodayPresenter> {
    fun openDetails(actionId: String)
    fun submitItems(items: List<TodayItem>)
    fun handleItemsError(error: BaseError?)
    fun handleLoading(loading: Boolean)
}