package com.ghuljr.onehabit.ui.intro.login

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import arrow.core.Option
import com.ghuljr.onehabit.R
import com.ghuljr.onehabit.databinding.ActivityLoginBinding
import com.ghuljr.onehabit.databinding.FragmentRegisterCredentialsBinding
import com.ghuljr.onehabit.ui.base.BaseActivity
import com.ghuljr.onehabit.ui.intro.register.RegisterActivity
import com.ghuljr.onehabit.ui.main.MainActivity
import com.ghuljr.onehabit_error.BaseEvent
import com.ghuljr.onehabit_error.LoadingEvent
import com.ghuljr.onehabit_error.ValidationError
import com.ghuljr.onehabit_error_android.event_handler.EventHandler
import com.ghuljr.onehabit_error_android.event_manager.SnackbarEventManager
import com.ghuljr.onehabit_error_android.extension.setErrorOption
import com.ghuljr.onehabit_presenter.intro.login.LoginPresenter
import com.ghuljr.onehabit_presenter.intro.login.LoginView
import com.ghuljr.onehabit_tools_android.extension.debouncedTextChanges
import com.ghuljr.onehabit_tools_android.extension.focusLostObservable
import com.ghuljr.onehabit_tools_android.extension.throttleClicks
import com.google.android.material.snackbar.Snackbar
import io.reactivex.rxjava3.core.Observable

class LoginActivity : BaseActivity<ActivityLoginBinding, LoginView, LoginPresenter>(), LoginView {

    private val eventHandler: EventHandler by lazy {
        EventHandler(listOf(SnackbarEventManager(
            eventView = viewBind.root,
            duration = Snackbar.LENGTH_INDEFINITE,
            actionWithName = { } to getString(R.string.ok)
        )), viewBind.loadingIndicator)
    }


    override fun bindView(): ActivityLoginBinding = ActivityLoginBinding.inflate(layoutInflater)
    override fun getPresenterView(): LoginView = this

    override fun emailChangedObservable(): Observable<String> =
        viewBind.emailInput.debouncedTextChanges()

    override fun passwordChangedObservable(): Observable<String> =
        viewBind.passwordInput.debouncedTextChanges()

    override fun emailFocusLostObservable(): Observable<Unit> =
        viewBind.emailInput.focusLostObservable()

    override fun passwordFocusLostObservable(): Observable<Unit> =
        viewBind.passwordInput.focusLostObservable()

    override fun setEmailErrorOption(error: Option<ValidationError>) {
        viewBind.emailLayout.setErrorOption(error)
    }

    override fun setPasswordErrorOption(error: Option<ValidationError>) {
        viewBind.passwordLayout.setErrorOption(error)
    }

    override fun signInClickedObservable(): Observable<Unit> =
        viewBind.signInButton.throttleClicks()

    override fun dontHaveAccountClickedObservable(): Observable<Unit> =
        viewBind.dontHaveAccountButton.throttleClicks()

    override fun resetPasswordClickedObservable(): Observable<Unit> =
        viewBind.resetPasswordButton.throttleClicks()

    override fun openRegisterFlow() {
        startActivity(RegisterActivity.newIntent(this))
        finish()
    }

    override fun openResetPassword() {
        TODO("Not yet implemented")
    }

    override fun handleSignInEvent(event: Option<BaseEvent>) {
        viewBind!!.signInButton.apply {
            text = if (event.orNull() == LoadingEvent) "" else getString(R.string.send)
            isClickable = event.orNull() != LoadingEvent
            isFocusable = event.orNull() != LoadingEvent
            isEnabled = event.orNull() != LoadingEvent
        }
        eventHandler!!.invoke(event)
    }

    override fun handleSuccess() {
        startActivity(MainActivity.newIntent(this))
        finishAffinity()
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, LoginActivity::class.java)
    }
}