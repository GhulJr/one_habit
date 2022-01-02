package com.ghuljr.onehabit_presenter.intro.fill_data

import com.ghuljr.onehabit_presenter.base.BaseView
import io.reactivex.rxjava3.core.Observable

interface FillUserDataView : BaseView<FillUserDataPresenter> {
    fun signOutClickObservable(): Observable<Unit>
    fun currentStepObservable(): Observable<CurrentStep>
    fun navigateBackClickObservable(): Observable<Unit>
    fun navigateBack()
    fun displayCounter(currentStepNumber: Int)

    enum class CurrentStep {
        VERIFY_EMAIL, EMAIL_VERIFIED, SET_DISPLAY_NAME
    }
}
