package com.ghuljr.onehabit_presenter.intro

import com.ghuljr.onehabit_presenter.base.BaseView
import io.reactivex.rxjava3.core.Observable

interface IntroView : BaseView<IntroPresenter> {

    fun signInClickObservable(): Observable<Unit>
    fun registerClickObservable(): Observable<Unit>

    fun openSignIn()
    fun openRegister()
}