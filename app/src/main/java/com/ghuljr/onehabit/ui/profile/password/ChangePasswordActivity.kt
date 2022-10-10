package com.ghuljr.onehabit.ui.profile.password

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import arrow.core.Option
import com.ghuljr.onehabit.R
import com.ghuljr.onehabit.databinding.ActivityChangeEmailBinding
import com.ghuljr.onehabit.databinding.ActivityChangePasswordBinding
import com.ghuljr.onehabit.ui.base.BaseActivity
import com.ghuljr.onehabit.ui.profile.email.ChangeEmailActivity
import com.ghuljr.onehabit.ui.profile.reauthenticate.ReAuthenticateActivity
import com.ghuljr.onehabit_error.BaseEvent
import com.ghuljr.onehabit_error_android.event_handler.EventHandler
import com.ghuljr.onehabit_error_android.event_manager.SnackbarEventManager
import com.ghuljr.onehabit_presenter.profile.email.ChangeEmailPresenter
import com.ghuljr.onehabit_presenter.profile.email.ChangeEmailView
import com.ghuljr.onehabit_presenter.profile.password.ChangePasswordPresenter
import com.ghuljr.onehabit_presenter.profile.password.ChangePasswordView
import com.google.android.material.snackbar.Snackbar

class ChangePasswordActivity :
    BaseActivity<ActivityChangePasswordBinding, ChangePasswordView, ChangePasswordPresenter>(),
    ChangePasswordView {

    private val reAuthResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK)
            presenter.changePassword()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBind.apply {
            sendButton.setOnClickListener { presenter.changePassword() }
            toolbar.setNavigationOnClickListener { onBackPressed() }
            passwordInput.doOnTextChanged { text, _, _, _ ->
                text?.toString()?.let { presenter.passwordInputChanged(it) }
            }
        }
    }

    override fun bindView(): ActivityChangePasswordBinding =
        ActivityChangePasswordBinding.inflate(layoutInflater)

    override fun getPresenterView(): ChangePasswordView = this

    override fun close() {
        onBackPressed()
    }

    override fun reAuthenticate() {
        reAuthResult.launch(ReAuthenticateActivity.intent(this))
    }

    override fun handleEvent(event: Option<BaseEvent>) {
        val eventManager = EventHandler(listOf(SnackbarEventManager(
            eventView = viewBind.root,
            duration = Snackbar.LENGTH_INDEFINITE,
            actionWithName = { } to getString(R.string.ok)
        )), viewBind.loadingIndicator)
        eventManager(event)
    }

    companion object {

        fun intent(from: Context) = Intent(from, ChangePasswordActivity::class.java)
    }
}