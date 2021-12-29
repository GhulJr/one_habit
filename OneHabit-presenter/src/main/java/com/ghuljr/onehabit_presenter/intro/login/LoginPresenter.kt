package com.ghuljr.onehabit_presenter.intro.login

import arrow.core.Option
import arrow.core.none
import arrow.core.zip
import com.ghuljr.onehabit_data.repository.LoggedInUserRepository
import com.ghuljr.onehabit_presenter.base.BasePresenter
import com.ghuljr.onehabit_presenter.validator.EmailValidator
import com.ghuljr.onehabit_presenter.validator.PasswordValidator
import com.ghuljr.onehabit_tools.base.network.LoginRequest
import com.ghuljr.onehabit_tools.di.ActivityScope
import com.ghuljr.onehabit_tools.di.ComputationScheduler
import com.ghuljr.onehabit_tools.di.UiScheduler
import com.ghuljr.onehabit_tools.extension.leftAsEvent
import com.ghuljr.onehabit_tools.extension.leftToOption
import com.ghuljr.onehabit_tools.extension.onlyDefined
import com.ghuljr.onehabit_tools.extension.toObservableWithLoading
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import javax.inject.Inject

@ActivityScope
class LoginPresenter @Inject constructor(
    private val emailValidator: EmailValidator,
    private val passwordValidator: PasswordValidator,
    private val loggedInUserRepository: LoggedInUserRepository,
    @ComputationScheduler private val computationScheduler: Scheduler,
    @UiScheduler private val uiScheduler: Scheduler
): BasePresenter<LoginView>() {

    private val loginDataOptionObservable: Observable<Option<LoginRequest>> =
        Observable.combineLatest(
            emailValidator.validatedEmailEitherObservable,
            passwordValidator.validatedPasswordEitherObservable
        ) { emailEither, passwordEither ->
            emailEither.zip(passwordEither) { email, password ->
                LoginRequest(email, password)
            }.orNone()
        }
            .subscribeOn(computationScheduler)
            .replay(1)
            .refCount()

    override fun subscribeToView(view: LoginView): Disposable {
        val signInClicked = view.signInClickedObservable().share()

        val validateEmailSignal = Observable.merge(view.emailFocusLostObservable(), signInClicked)
        val validatePasswordSignal = Observable.merge(view.passwordFocusLostObservable().share(), signInClicked)

        return CompositeDisposable(
            view.emailChangedObservable()
                .observeOn(uiScheduler)
                .subscribe {
                    view.setEmailErrorOption(none())
                    emailValidator.emailChanged(it)
                },
            view.passwordChangedObservable()
                .observeOn(uiScheduler)
                .subscribe {
                    view.setPasswordErrorOption(none())
                    passwordValidator.passwordChanged(it)
                },
            view.dontHaveAccountClickedObservable()
                .observeOn(uiScheduler)
                .subscribe { view.openRegisterFlow() },
            validateEmailSignal
                .switchMapSingle { emailValidator.validatedEmailEitherObservable.firstOrError() }
                .leftToOption()
                .observeOn(uiScheduler)
                .subscribe(view::setEmailErrorOption),
            validatePasswordSignal
                .switchMapSingle { passwordValidator.validatedPasswordEitherObservable.firstOrError() }
                .leftToOption()
                .observeOn(uiScheduler)
                .subscribe(view::setPasswordErrorOption),
            signInClicked
                .switchMap {
                    loginDataOptionObservable
                        .firstOrError()
                        .onlyDefined()
                        .flatMapSingle { loggedInUserRepository.signIn(it) }
                        .leftAsEvent()
                        .toObservableWithLoading()
                }
                .observeOn(uiScheduler)
                .subscribe {
                    view.handleSignInEvent(it.swap().orNone())    //TODO: create extensions for left mapping
                    it.map { view.handleSuccess() }
                },
            loginDataOptionObservable.subscribe()
        )
    }}