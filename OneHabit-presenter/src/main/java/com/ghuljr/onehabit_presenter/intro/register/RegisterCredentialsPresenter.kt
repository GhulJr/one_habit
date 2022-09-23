package com.ghuljr.onehabit_presenter.intro.register

import arrow.core.Option
import arrow.core.none
import arrow.core.zip
import com.ghuljr.onehabit_data.network.model.RegisterRequest
import com.ghuljr.onehabit_data.repository.LoggedInUserRepository
import com.ghuljr.onehabit_presenter.base.BasePresenter
import com.ghuljr.onehabit_presenter.validator.EmailValidator
import com.ghuljr.onehabit_presenter.validator.PasswordWithRepeatValidator
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
class RegisterCredentialsPresenter @Inject constructor(
    private val loggedInUserRepository: LoggedInUserRepository,
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
        val validateRepeatPasswordSignal = Observable.merge(
            view.repeatPasswordFocusLostObservable(),
            passwordFocusLost,
            sendClicked
        )

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
            view.repeatPasswordChangedObservable()
                .observeOn(uiScheduler)
                .subscribe {
                    view.setRepeatPasswordErrorOption(none())
                    passwordValidator.repeatPasswordChanged(it)
                },
            view.haveAccountClickedObservable()
                .observeOn(uiScheduler)
                .subscribe { view.openLoginFlow() },
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
            sendClicked
                .switchMap {
                    registerDataOptionObservable
                        .firstOrError()
                        .onlyDefined()
                        .flatMapSingle { loggedInUserRepository.register(it) }
                        .leftAsEvent()
                        .toObservableWithLoading()
                }
                .observeOn(uiScheduler)
                .subscribe {
                    view.handleSendEvent(
                        it.swap().orNone()
                    )    //TODO: create extensions for left mapping
                    it.map { view.handleSuccess() }
                },
            registerDataOptionObservable.subscribe()
        )
    }
}