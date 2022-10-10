package com.ghuljr.onehabit.ui.profile.reauthenticate

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import arrow.core.Option
import com.ghuljr.onehabit.R
import com.ghuljr.onehabit.databinding.ActivityReAuthenticateBinding
import com.ghuljr.onehabit.ui.base.BaseActivity
import com.ghuljr.onehabit.ui.intro.login.forgot_password.ForgotPasswordActivity
import com.ghuljr.onehabit.ui.intro.register.RegisterActivity
import com.ghuljr.onehabit.ui.main.MainActivity
import com.ghuljr.onehabit_error.BaseEvent
import com.ghuljr.onehabit_error.LoadingEvent
import com.ghuljr.onehabit_error.ValidationError
import com.ghuljr.onehabit_error_android.event_handler.EventHandler
import com.ghuljr.onehabit_error_android.event_manager.SnackbarEventManager
import com.ghuljr.onehabit_error_android.extension.setErrorOption
import com.ghuljr.onehabit_presenter.profile.reauthenticate.ReAuthenticatePresenter
import com.ghuljr.onehabit_presenter.profile.reauthenticate.ReAuthenticateView
import com.ghuljr.onehabit_tools_android.extension.debouncedTextChanges
import com.ghuljr.onehabit_tools_android.extension.focusLostObservable
import com.ghuljr.onehabit_tools_android.extension.throttleClicks
import com.google.android.material.snackbar.Snackbar
import io.reactivex.rxjava3.core.Observable

class ReAuthenticateActivity : BaseActivity<ActivityReAuthenticateBinding, ReAuthenticateView, ReAuthenticatePresenter>(), ReAuthenticateView {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBind.apply {
            toolbar.setNavigationOnClickListener { onBackPressed() }
        }
    }

    override fun bindView(): ActivityReAuthenticateBinding =
        ActivityReAuthenticateBinding.inflate(layoutInflater)

    override fun getPresenterView(): ReAuthenticateView = this

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
        viewBind.sendButton.throttleClicks()

    override fun handleSignInEvent(event: Option<BaseEvent>) {
        val eventHandler = EventHandler(listOf(SnackbarEventManager(
            eventView = viewBind.root,
            duration = Snackbar.LENGTH_INDEFINITE,
            actionWithName = { } to getString(R.string.ok)
        )), viewBind.loadingIndicator)
        eventHandler(event)
    }

    override fun handleSuccess() {
        setResult(Activity.RESULT_OK)
        finish()
    }

    companion object {
        fun intent(from: Context) = Intent(from, ReAuthenticateActivity::class.java)
    }
}