package com.ghuljr.onehabit_presenter.intro.launch

import com.ghuljr.onehabit_presenter.base.BasePresenter
import com.ghuljr.onehabit_data.network.service.LoggedInUserService
import com.ghuljr.onehabit_tools.di.ActivityScope
import com.ghuljr.onehabit_tools.di.UiScheduler
import com.ghuljr.onehabit_tools.extension.onlyFalse
import com.ghuljr.onehabit_tools.extension.onlyTrue
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import javax.inject.Inject

@ActivityScope
class LaunchPresenter @Inject constructor(
    private val userLoggedInUserService: LoggedInUserService,
    @UiScheduler private val uiScheduler: Scheduler
) : BasePresenter<LaunchView>() {

    private val userLoggedInObservable: Observable<Unit> = userLoggedInUserService.isUserLoggedInFlowable
        .onlyTrue()
        .toObservable()
        .observeOn(uiScheduler)

    private val userLoggedOutObservable: Observable<Unit> = userLoggedInUserService.isUserLoggedInFlowable
        .onlyFalse()
        .toObservable()
        .observeOn(uiScheduler)

    override fun subscribeToView(view: LaunchView): Disposable = CompositeDisposable(
        userLoggedInObservable.subscribe { view.userLoggedIn() },
        userLoggedOutObservable.subscribe { view.userLoggedOut() }
    )
}