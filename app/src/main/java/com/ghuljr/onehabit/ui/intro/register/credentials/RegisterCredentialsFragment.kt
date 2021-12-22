package com.ghuljr.onehabit.ui.intro.register.credentials

import android.view.LayoutInflater
import android.view.ViewGroup
import com.ghuljr.onehabit.databinding.FragmentRegisterCredentialsBinding
import com.ghuljr.onehabit.ui.base.BaseFragment
import com.ghuljr.onehabit_presenter.intro.register.RegisterCredentialsPresenter
import com.ghuljr.onehabit_presenter.intro.register.RegisterCredentialsView

class RegisterCredentialsFragment : BaseFragment<FragmentRegisterCredentialsBinding, RegisterCredentialsView, RegisterCredentialsPresenter>(), RegisterCredentialsView {

    override fun bindView(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentRegisterCredentialsBinding =
        FragmentRegisterCredentialsBinding.inflate(layoutInflater, container, false)

    override fun getPresenterView(): RegisterCredentialsView = this
}