package com.ghuljr.onehabit.ui.intro.fill_data.verify_email

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import arrow.core.Option
import arrow.core.some
import com.ghuljr.onehabit.R
import com.ghuljr.onehabit.databinding.FragmentVerifyEmailBinding
import com.ghuljr.onehabit.ui.base.BaseFragment
import com.ghuljr.onehabit_error.AuthError
import com.ghuljr.onehabit_error.BaseEvent
import com.ghuljr.onehabit_error_android.event_manager.SnackbarEventManager
import com.ghuljr.onehabit_presenter.intro.fill_data.verify_email.VerifyEmailPresenter
import com.ghuljr.onehabit_presenter.intro.fill_data.verify_email.VerifyEmailView
import com.ghuljr.onehabit_tools_android.extension.throttleClicks
import com.google.android.material.snackbar.Snackbar
import io.reactivex.rxjava3.core.Observable

class VerifyEmailFragment : BaseFragment<FragmentVerifyEmailBinding, VerifyEmailView, VerifyEmailPresenter>(), VerifyEmailView {

    private var snackbarEventManager: SnackbarEventManager? = null
    private var navController: NavController? = null

    override fun setUpView(viewBind: FragmentVerifyEmailBinding) {
        snackbarEventManager = SnackbarEventManager(
            eventView = viewBind.root,
            duration = Snackbar.LENGTH_INDEFINITE,
            actionWithName = { } to getString(R.string.ok)
        )

        navController = findNavController()
    }

    override fun destroyView() {
        snackbarEventManager = null
        navController = null
    }

    override fun bindView(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentVerifyEmailBinding = FragmentVerifyEmailBinding.inflate(layoutInflater, container, false)

    override fun getPresenterView(): VerifyEmailView = this

    override fun setEmailSending() {
        viewBind!!.apply {
            loadingIndicator.visibility = View.VISIBLE
            emailImage.setImageResource(R.drawable.ic_sending)
            emailTitle.text = getString(R.string.sending)
            emailSubtitle.text = getString(R.string.check_your_mailbox_description)
            retrySendButton.visibility = View.GONE
            resendButton.visibility = View.GONE
            checkEmailReceivedButton.visibility = View.GONE
        }
    }

    override fun setEmailSend() {
        viewBind!!.apply {
            loadingIndicator.visibility = View.GONE
            emailImage.setImageResource(R.drawable.ic_email_unread)
            emailTitle.text = getString(R.string.check_your_mailbox)
            emailSubtitle.text = getString(R.string.check_your_mailbox_description)
            retrySendButton.visibility = View.GONE
            resendButton.visibility = View.VISIBLE
            checkEmailReceivedButton.visibility = View.VISIBLE
        }
    }

    override fun setEmailSendError() {
        viewBind!!.apply {
            loadingIndicator.visibility = View.GONE
            emailImage.setImageResource(R.drawable.ic_error_send)
            emailTitle.text = getString(R.string.error_oops)
            emailSubtitle.text = getString(R.string.error_send_email_verification)
            retrySendButton.visibility = View.VISIBLE
            resendButton.visibility = View.GONE
            checkEmailReceivedButton.visibility = View.GONE
        }
    }

    override fun displayResend() {
        viewBind!!.apply {
            resendButton.text = getString(R.string.didnt_received_anything)
            resendButton.isClickable = true
            resendButton.isFocusable = true
            resendButton.isEnabled = true
        }
    }

    override fun setTimerToResendValue(value: String) {
        viewBind!!.apply {
            resendButton.text = getString(R.string.time_remaining_to_resend, value)
            resendButton.isClickable = false
            resendButton.isFocusable = false
            resendButton.isEnabled = false
        }
    }

    override fun handleVerifyEmailCheck(event: Option<BaseEvent>) {
        snackbarEventManager?.handleEvent(event)
    }

    override fun emailVerified() {
        navController?.navigate(VerifyEmailFragmentDirections.actionVerifyEmailFragmentToVerifyEmailFinishedFragment())
    }

    override fun checkEmailVerificationClickedObservable(): Observable<Unit> = viewBind!!.checkEmailReceivedButton.throttleClicks()
    override fun resendClickedObservable(): Observable<Unit> = viewBind!!.resendButton.throttleClicks()
    override fun retrySendButtonClickedObservable(): Observable<Unit> = viewBind!!.retrySendButton.throttleClicks()

}