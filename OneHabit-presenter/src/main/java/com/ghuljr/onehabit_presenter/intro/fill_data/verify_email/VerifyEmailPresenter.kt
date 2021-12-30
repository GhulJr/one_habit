package com.ghuljr.onehabit_presenter.intro.fill_data.verify_email

import arrow.core.right
import com.ghuljr.onehabit_data.repository.LoggedInUserRepository
import com.ghuljr.onehabit_error.BaseEvent
import com.ghuljr.onehabit_error.LoadingEvent
import com.ghuljr.onehabit_presenter.base.BasePresenter
import com.ghuljr.onehabit_tools.di.ComputationScheduler
import com.ghuljr.onehabit_tools.di.FragmentScope
import com.ghuljr.onehabit_tools.di.UiScheduler
import com.ghuljr.onehabit_tools.extension.*
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import javax.inject.Inject

@FragmentScope
class VerifyEmailPresenter @Inject constructor(
    private val repository: LoggedInUserRepository,
    @ComputationScheduler private val computationScheduler: Scheduler,
    @UiScheduler private val uiScheduler: Scheduler
) : BasePresenter<VerifyEmailView>() {
    override fun subscribeToView(view: VerifyEmailView): Disposable {
        val retrySendClicked = view.retrySendButtonClickedObservable()
        val resendClicked = view.resendClickedObservable()
        return CompositeDisposable(
            repository.userFlowable
                .onlyDefined()
                .filter { it.isEmailVerified }
                .observeOn(uiScheduler)
                .subscribe { view.emailVerified() },

            Observable.merge(resendClicked, retrySendClicked)
                .map { true }
                .startWithItem(false)
                .switchMap { forceResend ->
                    repository.isEmailVerificationSendFlowable.firstOrError()
                        .flatMapObservable {
                            if (forceResend || !it) {
                                repository.sendEmailVerification()
                                    .leftAsEvent()
                                    .toObservableWithLoading()
                            } else {
                                Observable.just(true.right())
                            }
                        }
                }
                .observeOn(uiScheduler)
                .subscribe {
                    when {
                        it.isLeft() && it.swap().orNull() == LoadingEvent -> view.setEmailSending()
                        it.isLeft() -> view.setEmailSendError()
                        else -> view.setEmailSend()
                    }
                },
            resendClicked
                .toCountdownTimer(COUNTDOWN_TIME, computationScheduler)
                .observeOn(uiScheduler)
                .subscribe {
                    if (it == 0L) view.displayResend()
                    else view.setTimerToResendValue(it.toString())
                },
            view.checkEmailVerificationClickedObservable()
                .switchMapSingle { repository.refreshUser() }
                .observeOn(uiScheduler)
                .leftToOption()
                .subscribe { error -> view.handleVerifyEmailCheck(error.map { it as BaseEvent }) }
        )
    }

    companion object {
        const val COUNTDOWN_TIME = 45L
    }
}