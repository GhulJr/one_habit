package com.ghuljr.onehabit_presenter.intro.login.forgot_password

import arrow.core.Option
import com.ghuljr.onehabit_error.BaseEvent
import com.ghuljr.onehabit_error.ValidationError
import com.ghuljr.onehabit_presenter.base.BaseView
import io.reactivex.rxjava3.core.Observable

interface ForgotPasswordView : BaseView<ForgotPasswordPresenter> {
    fun emailChangedObservable(): Observable<String>
    fun sendClickedObservable(): Observable<Unit>
    fun navigateBackClickedObservable(): Observable<Unit>

    fun handleSendSuccess()
    fun handleValidateEmailError(error: Option<ValidationError>)
    fun handleSendEvent(event: Option<BaseEvent>)
    fun finishResetingPassword()
}