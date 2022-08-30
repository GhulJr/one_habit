package com.ghuljr.onehabit.ui.intro.register.credentials

import android.view.LayoutInflater
import android.view.ViewGroup
import arrow.core.Option
import com.ghuljr.onehabit.R
import com.ghuljr.onehabit.databinding.FragmentRegisterCredentialsBinding
import com.ghuljr.onehabit.ui.base.BaseFragment
import com.ghuljr.onehabit.ui.intro.login.LoginActivity
import com.ghuljr.onehabit.ui.main.MainActivity
import com.ghuljr.onehabit_error.BaseEvent
import com.ghuljr.onehabit_error.LoadingEvent
import com.ghuljr.onehabit_error.ValidationError
import com.ghuljr.onehabit_error_android.event_handler.EventHandler
import com.ghuljr.onehabit_error_android.event_manager.SnackbarEventManager
import com.ghuljr.onehabit_error_android.extension.setErrorOption
import com.ghuljr.onehabit_presenter.intro.register.RegisterCredentialsPresenter
import com.ghuljr.onehabit_presenter.intro.register.RegisterCredentialsView
import com.ghuljr.onehabit_tools_android.extension.debouncedTextChanges
import com.ghuljr.onehabit_tools_android.extension.focusLostObservable
import com.ghuljr.onehabit_tools_android.extension.throttleClicks
import com.ghuljr.onehabit_tools_android.extension.throttleNavigationClicks
import com.google.android.material.snackbar.Snackbar
import io.reactivex.rxjava3.core.Observable

class RegisterCredentialsFragment :
    BaseFragment<FragmentRegisterCredentialsBinding, RegisterCredentialsView, RegisterCredentialsPresenter>(),
    RegisterCredentialsView {

    private var eventHandler: EventHandler? = null

    override fun setUpView(viewBind: FragmentRegisterCredentialsBinding) {
        eventHandler = EventHandler(listOf(SnackbarEventManager(
            eventView = viewBind.root,
            duration = Snackbar.LENGTH_INDEFINITE,
            actionWithName = { } to getString(R.string.ok)
        )), viewBind.loadingIndicator)
    }

    override fun destroyView() {
        eventHandler = null
    }

    override fun bindView(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentRegisterCredentialsBinding =
        FragmentRegisterCredentialsBinding.inflate(layoutInflater, container, false)

    override fun getPresenterView(): RegisterCredentialsView = this

    override fun emailChangedObservable(): Observable<String> =
        viewBind!!.emailInput.debouncedTextChanges()

    override fun passwordChangedObservable(): Observable<String> =
        viewBind!!.passwordInput.debouncedTextChanges()

    override fun repeatPasswordChangedObservable(): Observable<String> =
        viewBind!!.repeatPasswordInput.debouncedTextChanges()

    override fun emailFocusLostObservable(): Observable<Unit> =
        viewBind!!.emailInput.focusLostObservable()

    override fun passwordFocusLostObservable(): Observable<Unit> =
        viewBind!!.passwordInput.focusLostObservable()

    override fun repeatPasswordFocusLostObservable(): Observable<Unit> =
        viewBind!!.repeatPasswordInput.focusLostObservable()

    override fun navigationBackClickedObservable(): Observable<Unit> = viewBind!!.toolbar.throttleNavigationClicks()

    override fun setEmailErrorOption(error: Option<ValidationError>) {
        viewBind!!.emailLayout.setErrorOption(error)
    }

    override fun setPasswordErrorOption(error: Option<ValidationError>) {
        viewBind!!.passwordLayout.setErrorOption(error)
    }

    override fun setRepeatPasswordErrorOption(error: Option<ValidationError>) {
        viewBind!!.repeatPasswordLayout.setErrorOption(error)
    }

    override fun navigateBack() {
        requireActivity().finish()
    }

    override fun sendClickedObservable(): Observable<Unit> = viewBind!!.sendButton.throttleClicks()
    override fun haveAccountClickedObservable(): Observable<Unit> =
        viewBind!!.signInButton.throttleClicks()

    override fun openLoginFlow() {
        startActivity(LoginActivity.newIntent(requireContext()))
        requireActivity().finish()
    }

    override fun handleSendEvent(event: Option<BaseEvent>) {
        viewBind!!.sendButton.apply {
            text = if (event.orNull() == LoadingEvent) "" else getString(R.string.send)
            isClickable = event.orNull() != LoadingEvent
            isFocusable = event.orNull() != LoadingEvent
            isEnabled = event.orNull() != LoadingEvent
        }
        eventHandler!!.invoke(event)
    }

    override fun handleSuccess() {
        startActivity(MainActivity.newIntent(requireContext()))
        requireActivity().finishAffinity()
    }
}