package com.ghuljr.onehabit.ui.intro.login.forgot_password

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import arrow.core.Option
import com.ghuljr.onehabit.R
import com.ghuljr.onehabit.databinding.ActivityForgotPasswordBinding
import com.ghuljr.onehabit.ui.base.BaseActivity
import com.ghuljr.onehabit_error.BaseEvent
import com.ghuljr.onehabit_error.LoadingEvent
import com.ghuljr.onehabit_error.ValidationError
import com.ghuljr.onehabit_error_android.event_handler.EventHandler
import com.ghuljr.onehabit_error_android.event_manager.SnackbarEventManager
import com.ghuljr.onehabit_error_android.extension.setErrorOption
import com.ghuljr.onehabit_presenter.intro.login.forgot_password.ForgotPasswordPresenter
import com.ghuljr.onehabit_presenter.intro.login.forgot_password.ForgotPasswordView
import com.ghuljr.onehabit_tools_android.extension.debouncedTextChanges
import com.ghuljr.onehabit_tools_android.extension.throttleClicks
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.rxbinding4.appcompat.navigationClicks
import io.reactivex.rxjava3.core.Observable

class ForgotPasswordActivity :
    BaseActivity<ActivityForgotPasswordBinding, ForgotPasswordView, ForgotPasswordPresenter>(),
    ForgotPasswordView {

    private val eventHandler: EventHandler by lazy {
        EventHandler(listOf(SnackbarEventManager(
            eventView = viewBind.root,
            duration = Snackbar.LENGTH_INDEFINITE,
            actionWithName = { } to getString(R.string.ok)
        )), viewBind.loadingIndicator)
    }

    override fun onBackPressed() {
        presenter.finish()
    }

    override fun bindView(): ActivityForgotPasswordBinding =
        ActivityForgotPasswordBinding.inflate(layoutInflater)

    override fun getPresenterView(): ForgotPasswordView = this

    override fun emailChangedObservable(): Observable<String> =
        viewBind.emailInput.debouncedTextChanges()

    override fun sendClickedObservable(): Observable<Unit> = viewBind.sendButton.throttleClicks()
    override fun navigateBackClickedObservable(): Observable<Unit> =
        viewBind.toolbar.navigationClicks()

    override fun handleSendSuccess() {
        AlertDialog.Builder(this, R.style.OneHabit_AlertDialog)
            .setTitle(R.string.email_sent)
            .setMessage(R.string.email_sent_description)
            .setCancelable(false)
            .setPositiveButton(R.string.got_it) { _, _ -> presenter.finish() }
            .show()
    }

    override fun handleValidateEmailError(error: Option<ValidationError>) {
        viewBind.emailLayout.setErrorOption(error)
    }

    override fun handleSendEvent(event: Option<BaseEvent>) {
        //TODO: maybe create a class like `ButtonWithLoading`
        viewBind.sendButton.apply {
            text = if (event.orNull() == LoadingEvent) "" else getString(R.string.send)
            isClickable = event.orNull() != LoadingEvent
            isFocusable = event.orNull() != LoadingEvent
            isEnabled = event.orNull() != LoadingEvent
        }
        eventHandler.invoke(event)
    }

    override fun finishResetingPassword() {
        finish()
    }

    companion object {
        fun newIntent(context: Context): Intent =
            Intent(context, ForgotPasswordActivity::class.java)
    }
}