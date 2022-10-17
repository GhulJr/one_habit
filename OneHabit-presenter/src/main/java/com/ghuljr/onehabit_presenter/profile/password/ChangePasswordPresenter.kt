package com.ghuljr.onehabit_presenter.profile.password

import arrow.core.left
import arrow.core.none
import arrow.core.some
import com.ghuljr.onehabit_data.repository.LoggedInUserRepository
import com.ghuljr.onehabit_error.AuthError
import com.ghuljr.onehabit_error.LoadingEvent
import com.ghuljr.onehabit_presenter.base.BasePresenter
import com.ghuljr.onehabit_tools.di.ActivityScope
import com.ghuljr.onehabit_tools.di.ComputationScheduler
import com.ghuljr.onehabit_tools.di.UiScheduler
import com.ghuljr.onehabit_tools.extension.startWithLoading
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@ActivityScope
class ChangePasswordPresenter @Inject constructor(
    @UiScheduler private val uiScheduler: Scheduler,
    @ComputationScheduler private val computationScheduler: Scheduler,
    private val loggedInUserRepository: LoggedInUserRepository
) : BasePresenter<ChangePasswordView>() {

    private val setNewPasswordSubject = PublishSubject.create<Unit>()
    private val newPasswordSubject = BehaviorSubject.create<String>()

    override fun subscribeToView(view: ChangePasswordView): Disposable = CompositeDisposable(
        setNewPasswordSubject
            .throttleFirst(500L, TimeUnit.MILLISECONDS, computationScheduler)
            .withLatestFrom(newPasswordSubject) { _, newName -> newName }
            .switchMap { newName ->
                loggedInUserRepository.setPassword(newName)
                    .toObservable()
                    .startWithLoading()
            }
            .observeOn(uiScheduler)
            .subscribe {
                it.fold(
                    ifRight = {
                        view.handleEvent(none())
                        loggedInUserRepository.signOut()
                    },
                    ifLeft = {
                        if (it is AuthError.RequireReAuthentication)
                            view.reAuthenticate()
                        else
                            view.handleEvent(it.some())
                    }
                )
            }
    )

    fun changePassword() = setNewPasswordSubject.onNext(Unit)
    fun passwordInputChanged(password: String) = newPasswordSubject.onNext(password)
}