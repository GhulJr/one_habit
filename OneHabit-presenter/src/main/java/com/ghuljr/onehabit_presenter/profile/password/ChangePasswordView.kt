package com.ghuljr.onehabit_presenter.profile.password

import arrow.core.Option
import com.ghuljr.onehabit_error.BaseEvent
import com.ghuljr.onehabit_presenter.base.BaseView

interface ChangePasswordView : BaseView<ChangePasswordPresenter> {
    fun close()
    fun reAuthenticate()
    fun handleEvent(event: Option<BaseEvent>)
}