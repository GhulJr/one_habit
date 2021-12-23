package com.ghuljr.onehabit_presenter.intro.register

import arrow.core.Option
import com.ghuljr.onehabit_error.ValidationError
import com.ghuljr.onehabit_presenter.base.BaseView
import io.reactivex.rxjava3.core.Observable

interface RegisterCredentialsView : BaseView<RegisterCredentialsPresenter> {

    fun emailChangedObservable(): Observable<String>
    fun passwordChangedObservable(): Observable<String>
    fun repeatPasswordChangedObservable(): Observable<String>

    fun emailFocusLostObservable() : Observable<Unit>
    fun passwordFocusLostObservable() : Observable<Unit>
    fun repeatPasswordFocusLostObservable(): Observable<Unit>

    fun setEmailErrorOption(error: Option<ValidationError>)
    fun setPasswordErrorOption(error: Option<ValidationError>)
    fun setRepeatPasswordErrorOption(error: Option<ValidationError>)

    fun sendClickedObservable() : Observable<Unit>
}