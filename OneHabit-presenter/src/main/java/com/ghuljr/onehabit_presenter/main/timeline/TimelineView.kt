package com.ghuljr.onehabit_presenter.main.timeline

import com.ghuljr.onehabit_presenter.base.BaseView

interface TimelineView : BaseView<TimelinePresenter> {

    fun submitItems(items: List<TimelineItem>)
}