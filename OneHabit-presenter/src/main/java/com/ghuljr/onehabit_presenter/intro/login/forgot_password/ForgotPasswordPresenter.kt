package com.ghuljr.onehabit_presenter.intro.login.forgot_password

import arrow.core.none
import com.ghuljr.onehabit_data.repository.LoggedInUserRepository
import com.ghuljr.onehabit_presenter.base.BasePresenter
import com.ghuljr.onehabit_presenter.validator.EmailValidator
import com.ghuljr.onehabit_tools.di.ActivityScope
import com.ghuljr.onehabit_tools.di.UiScheduler
import com.ghuljr.onehabit_tools.extension.leftAsEvent
import com.ghuljr.onehabit_tools.extension.leftToOption
import com.ghuljr.onehabit_tools.extension.onlyRight
import com.ghuljr.onehabit_tools.extension.toObservableWithLoading
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.PublishSubject
import javax.inject.Inject

@ActivityScope
class ForgotPasswordPresenter @Inject constructor(
    private val loggedInUserRepository: LoggedInUserRepository,
    private val emailValidator: EmailValidator,
    @UiScheduler private val uiScheduler: Scheduler
) : BasePresenter<ForgotPasswordView>() {

    private val navigationBackClickSubject = PublishSubject.create<Unit>()

    override fun subscribeToView(view: ForgotPasswordView): Disposable {
        val sendClick = view.sendClickedObservable().share()
        return CompositeDisposable(
            view.emailChangedObservable()
                .observeOn(uiScheduler)
                .subscribe {
                    view.handleValidateEmailError(none())
                    emailValidator.emailChanged(it)
                },
            Observable.merge(navigationBackClickSubject, view.navigateBackClickedObservable())
                .observeOn(uiScheduler)
                .subscribe { view.navigateBack() },
            sendClick
                .switchMapSingle {
                    emailValidator.validatedEmailEitherObservable
                        .firstOrError()
                        .leftToOption()
                }
                .subscribe { view.handleValidateEmailError(it) },
            sendClick
                .switchMap {
                    emailValidator.validatedEmailEitherObservable
                        .firstOrError()
                        .onlyRight()
                        .flatMapObservable {
                            loggedInUserRepository.resetPassword(it)
                                .leftAsEvent()
                                .toObservableWithLoading()
                        }
                }
                .subscribe {
                    view.handleSendEvent(it.swap().orNone())
                    it.map { view.handleSendSuccess() }
                },
            emailValidator.validatedEmailEitherObservable.subscribe()
        )
    }

    fun backClicked(): Unit = navigationBackClickSubject.onNext(Unit)
}