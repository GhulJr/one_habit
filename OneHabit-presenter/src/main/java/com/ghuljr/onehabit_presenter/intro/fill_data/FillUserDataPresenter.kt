package com.ghuljr.onehabit_presenter.intro.fill_data

import com.ghuljr.onehabit_data.repository.LoggedInUserRepository
import com.ghuljr.onehabit_presenter.base.BasePresenter
import com.ghuljr.onehabit_tools.di.ActivityScope
import com.ghuljr.onehabit_tools.di.UiScheduler
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.PublishSubject
import javax.inject.Inject

@ActivityScope
class FillUserDataPresenter @Inject constructor(
    private val repository: LoggedInUserRepository,
    @UiScheduler private val uiScheduler: Scheduler
): BasePresenter<FillUserDataView>() {

    private val navigateBackClickSubject = PublishSubject.create<Unit>()

    override fun subscribeToView(view: FillUserDataView): Disposable = CompositeDisposable(
        view.signOutClickObservable()
            .observeOn(uiScheduler)
            .subscribe { repository.signOut() },
        Observable.merge(navigateBackClickSubject, view.navigateBackClickObservable())
            .observeOn(uiScheduler)
            .subscribe { view.navigateBack() },
        view.currentStepObservable()
            .observeOn(uiScheduler)
            .subscribe { view.displayCounter(FillUserDataView.CurrentStep.values().indexOf(it) + 1) }
    )

    fun navigateBackClicked(): Unit = navigateBackClickSubject.onNext(Unit)
}