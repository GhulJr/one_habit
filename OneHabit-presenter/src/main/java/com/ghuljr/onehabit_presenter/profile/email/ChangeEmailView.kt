package com.ghuljr.onehabit_presenter.profile.email

import arrow.core.Option
import com.ghuljr.onehabit_error.BaseEvent
import com.ghuljr.onehabit_presenter.base.BaseView

interface ChangeEmailView : BaseView<ChangeEmailPresenter> {
    fun close()
    fun reAuthenticate()
    fun handleEvent(event: Option<BaseEvent>)
}