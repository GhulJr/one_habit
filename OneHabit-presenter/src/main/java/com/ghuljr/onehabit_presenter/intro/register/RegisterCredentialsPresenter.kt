package com.ghuljr.onehabit_presenter.intro.register

import arrow.core.Option
import arrow.core.zip
import com.ghuljr.onehabit_presenter.base.BasePresenter
import com.ghuljr.onehabit_presenter.validator.EmailValidator
import com.ghuljr.onehabit_presenter.validator.PasswordWithRepeatValidator
import com.ghuljr.onehabit_tools.di.ComputationScheduler
import com.ghuljr.onehabit_tools.di.FragmentScope
import com.ghuljr.onehabit_tools.di.UiScheduler
import com.ghuljr.onehabit_tools.extension.leftToOption
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import javax.inject.Inject

@FragmentScope
class RegisterCredentialsPresenter @Inject constructor(
    private val emailValidator: EmailValidator,
    private val passwordValidator: PasswordWithRepeatValidator,
    @ComputationScheduler private val computationScheduler: Scheduler,
    @UiScheduler private val uiScheduler: Scheduler
) : BasePresenter<RegisterCredentialsView>() {

    private val registerDataOptionObservable: Observable<Option<RegisterRequest>> =
        Observable.combineLatest(
            emailValidator.validatedEmailEitherObservable,
            passwordValidator.validatedPasswordEitherObservable,
            passwordValidator.validatedRepeatPasswordEitherObservable
        ) { emailEither, passwordEither, repeatEither ->
            emailEither.zip(passwordEither, repeatEither) { email, password, _ ->
                RegisterRequest(email, password)
            }.orNone()
        }
            .subscribeOn(computationScheduler)
            .replay(1)
            .refCount()

    override fun subscribeToView(view: RegisterCredentialsView): Disposable {
        val passwordFocusLost = view.passwordFocusLostObservable().share()
        val sendClicked = view.sendClickedObservable().share()

        val validateEmailSignal = Observable.merge(view.emailFocusLostObservable(), sendClicked)
        val validatePasswordSignal = Observable.merge(passwordFocusLost, sendClicked)
        val validateRepeatPasswordSignal = Observable.merge(view.repeatPasswordFocusLostObservable(), passwordFocusLost, sendClicked)

        return CompositeDisposable(
            view.emailChangedObservable()
                .subscribe(emailValidator::emailChanged),
            view.passwordChangedObservable()
                .subscribe(passwordValidator::passwordChanged),
            view.repeatPasswordChangedObservable()
                .subscribe(passwordValidator::repeatPasswordChanged),
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
            validateRepeatPasswordSignal
                .switchMapSingle { passwordValidator.validatedRepeatPasswordEitherObservable.firstOrError() }
                .leftToOption()
                .observeOn(uiScheduler)
                .subscribe(view::setRepeatPasswordErrorOption),
            registerDataOptionObservable.subscribe()
        )
    }
}

data class RegisterRequest(val email: String, val password: String)