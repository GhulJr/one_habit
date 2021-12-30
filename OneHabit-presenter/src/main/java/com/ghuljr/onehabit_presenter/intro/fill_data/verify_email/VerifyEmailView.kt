package com.ghuljr.onehabit_presenter.intro.fill_data.verify_email

import arrow.core.Option
import com.ghuljr.onehabit_error.BaseEvent
import com.ghuljr.onehabit_presenter.base.BaseView
import io.reactivex.rxjava3.core.Observable

interface VerifyEmailView : BaseView<VerifyEmailPresenter> {

    fun setEmailSending()
    fun setEmailSend()
    fun setEmailSendError()

    fun displayResend()
    fun setTimerToResendValue(value: String)

    fun handleVerifyEmailCheck(event: Option<BaseEvent>)

    fun emailVerified()

    fun checkEmailVerificationClickedObservable(): Observable<Unit>
    fun resendClickedObservable(): Observable<Unit>
    fun retrySendButtonClickedObservable(): Observable<Unit>
}