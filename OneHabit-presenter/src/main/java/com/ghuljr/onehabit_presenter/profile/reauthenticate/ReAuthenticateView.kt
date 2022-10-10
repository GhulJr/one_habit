package com.ghuljr.onehabit_presenter.profile.reauthenticate

import arrow.core.Option
import com.ghuljr.onehabit_error.BaseEvent
import com.ghuljr.onehabit_error.ValidationError
import com.ghuljr.onehabit_presenter.base.BaseView
import io.reactivex.rxjava3.core.Observable

interface ReAuthenticateView : BaseView<ReAuthenticatePresenter> {
    fun emailChangedObservable(): Observable<String>
    fun passwordChangedObservable(): Observable<String>

    fun emailFocusLostObservable() : Observable<Unit>
    fun passwordFocusLostObservable() : Observable<Unit>

    fun setEmailErrorOption(error: Option<ValidationError>)
    fun setPasswordErrorOption(error: Option<ValidationError>)

    fun signInClickedObservable() : Observable<Unit>

    fun handleSignInEvent(event: Option<BaseEvent>)
    fun handleSuccess()
}