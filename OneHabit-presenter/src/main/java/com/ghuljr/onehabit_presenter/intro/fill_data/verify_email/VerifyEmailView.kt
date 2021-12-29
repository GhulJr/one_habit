package com.ghuljr.onehabit_presenter.intro.fill_data.verify_email

import com.ghuljr.onehabit_presenter.base.BaseView
import io.reactivex.rxjava3.core.Observable

interface VerifyEmailView : BaseView<VerifyEmailPresenter> {

    fun emailSending()
    fun emailSend()
    fun emailSendError()

    fun displayResend()
    fun setTimerToResendValue(value: String)

    fun displayEmailNotVerifiedYetMessage()

    fun emailVerified()

    fun checkEmailVerificationClickedObservable(): Observable<Unit>
    fun resendClickedObservable(): Observable<Unit>
    fun retrySendButtonClickedObservable(): Observable<Unit>
}