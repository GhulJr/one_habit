package com.ghuljr.onehabit.ui.intro.register.credentials

import android.view.LayoutInflater
import android.view.ViewGroup
import arrow.core.Option
import com.ghuljr.onehabit.databinding.FragmentRegisterCredentialsBinding
import com.ghuljr.onehabit.ui.base.BaseFragment
import com.ghuljr.onehabit_error.ValidationError
import com.ghuljr.onehabit_presenter.intro.register.RegisterCredentialsPresenter
import com.ghuljr.onehabit_presenter.intro.register.RegisterCredentialsView
import com.ghuljr.onehabit_tools_android.extension.debouncedTextChanges
import com.ghuljr.onehabit_tools_android.extension.focusLostObservable
import com.ghuljr.onehabit_tools_android.extension.setErrorOption
import com.ghuljr.onehabit_tools_android.extension.throttleClicks
import io.reactivex.rxjava3.core.Observable

class RegisterCredentialsFragment : BaseFragment<FragmentRegisterCredentialsBinding, RegisterCredentialsView, RegisterCredentialsPresenter>(), RegisterCredentialsView {

    override fun bindView(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentRegisterCredentialsBinding =
        FragmentRegisterCredentialsBinding.inflate(layoutInflater, container, false)

    override fun getPresenterView(): RegisterCredentialsView = this

    override fun emailChangedObservable(): Observable<String> = viewBind!!.emailInput.debouncedTextChanges()
    override fun passwordChangedObservable(): Observable<String> = viewBind!!.passwordInput.debouncedTextChanges()
    override fun repeatPasswordChangedObservable(): Observable<String> = viewBind!!.repeatPasswordInput.debouncedTextChanges()

    override fun emailFocusLostObservable(): Observable<Unit> = viewBind!!.emailInput.focusLostObservable()
    override fun passwordFocusLostObservable(): Observable<Unit> = viewBind!!.passwordInput.focusLostObservable()
    override fun repeatPasswordFocusLostObservable(): Observable<Unit> = viewBind!!.repeatPasswordInput.focusLostObservable()

    override fun setEmailErrorOption(error: Option<ValidationError>) {
        viewBind!!.emailLayout.setErrorOption(error)
    }

    override fun setPasswordErrorOption(error: Option<ValidationError>) {
        viewBind!!.passwordLayout.setErrorOption(error)
    }

    override fun setRepeatPasswordErrorOption(error: Option<ValidationError>) {
        viewBind!!.repeatPasswordLayout.setErrorOption(error)
    }

    override fun sendClickedObservable(): Observable<Unit> = viewBind!!.sendButton.throttleClicks()
}