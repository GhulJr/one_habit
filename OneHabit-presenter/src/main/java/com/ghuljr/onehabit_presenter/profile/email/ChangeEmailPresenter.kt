package com.ghuljr.onehabit_presenter.profile.email

import arrow.core.left
import arrow.core.none
import arrow.core.some
import com.ghuljr.onehabit_data.repository.LoggedInUserRepository
import com.ghuljr.onehabit_error.BaseEvent
import com.ghuljr.onehabit_error.LoadingEvent
import com.ghuljr.onehabit_error.RequireReAuthenticationEvent
import com.ghuljr.onehabit_presenter.base.BasePresenter
import com.ghuljr.onehabit_tools.di.ActivityScope
import com.ghuljr.onehabit_tools.di.ComputationScheduler
import com.ghuljr.onehabit_tools.di.UiScheduler
import com.ghuljr.onehabit_tools.extension.mapLeft
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@ActivityScope
class ChangeEmailPresenter @Inject constructor(
    @UiScheduler private val uiScheduler: Scheduler,
    @ComputationScheduler private val computationScheduler: Scheduler,
    private val loggedInUserRepository: LoggedInUserRepository
) : BasePresenter<ChangeEmailView>() {

    private val setNewEmailSubject = PublishSubject.create<Unit>()
    private val newEmailSubject = BehaviorSubject.create<String>()

    override fun subscribeToView(view: ChangeEmailView): Disposable = CompositeDisposable(
        setNewEmailSubject
            .throttleFirst(500L, TimeUnit.MILLISECONDS, computationScheduler)
            .withLatestFrom(newEmailSubject) { _, newName -> newName }
            .switchMap { newName ->
                loggedInUserRepository.setEmail(newName)
                    .toObservable()
                    .startWithItem(LoadingEvent.left())
            }
            .observeOn(uiScheduler)
            .subscribe {
                it.fold(
                    ifRight = {
                        view.handleEvent(none())
                        view.close()
                    },
                    ifLeft = {
                        if (it is RequireReAuthenticationEvent)
                            view.reAuthenticate()
                        else
                            view.handleEvent(it.some())
                    }

                )
            }
    )

    fun changeEmail() = setNewEmailSubject.onNext(Unit)
    fun emailInputChanged(email: String) = newEmailSubject.onNext(email)
}