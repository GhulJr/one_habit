package com.ghuljr.onehabit_presenter.intro

import com.ghuljr.onehabit_presenter.base.BasePresenter
import com.ghuljr.onehabit_tools.di.ActivityScope
import com.ghuljr.onehabit_tools.di.UiScheduler
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import javax.inject.Inject

@ActivityScope
class IntroPresenter @Inject constructor(
    @UiScheduler private val uiScheduler: Scheduler
) : BasePresenter<IntroView>() {

    override fun subscribeToView(view: IntroView): Disposable = CompositeDisposable(
        view.signInClickObservable()
            .observeOn(uiScheduler)
            .subscribe { view.openSignIn() },
        view.registerClickObservable()
            .observeOn(uiScheduler)
            .subscribe { view.openRegister() }
    )
}