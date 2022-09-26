package com.ghuljr.onehabit_presenter.main.today.info

import com.ghuljr.onehabit_presenter.base.BaseView
import com.ghuljr.onehabit_presenter.main.today.ActionInfoItem

interface ActionInfoView : BaseView<ActionInfoPresenter> {
    fun displayActionInfo(actionInfoItem: ActionInfoItem)
}