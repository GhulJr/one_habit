package com.ghuljr.onehabit_presenter.profile.name

import arrow.core.Option
import com.ghuljr.onehabit_error.BaseEvent
import com.ghuljr.onehabit_presenter.base.BaseView

interface ChangeDisplayNameView : BaseView<ChangeDisplayNamePresenter> {

    fun close()
    fun handleEvent(event: Option<BaseEvent>)
}