package com.ghuljr.onehabit_presenter.intro.fill_data.verify_email

import com.ghuljr.onehabit_presenter.base.BaseView
import io.reactivex.rxjava3.core.Observable

interface VerifyEmailFinishedView: BaseView<VerifyEmailFinishedPresenter> {
    fun nextClickObservable(): Observable<Unit>
    fun goNext()
}