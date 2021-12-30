package com.ghuljr.onehabit_presenter.intro.fill_data.verify_email

import com.ghuljr.onehabit_presenter.base.BasePresenter
import com.ghuljr.onehabit_tools.di.FragmentScope
import com.ghuljr.onehabit_tools.di.UiScheduler
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.Disposable
import javax.inject.Inject

@FragmentScope
class VerifyEmailFinishedPresenter @Inject constructor(
    @UiScheduler private val uiScheduler: Scheduler
): BasePresenter<VerifyEmailFinishedView>() {
    override fun subscribeToView(view: VerifyEmailFinishedView): Disposable = view.nextClickObservable()
        .observeOn(uiScheduler)
        .subscribe { view.goNext() }
}