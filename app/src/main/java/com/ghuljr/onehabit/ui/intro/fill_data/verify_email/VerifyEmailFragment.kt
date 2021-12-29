package com.ghuljr.onehabit.ui.intro.fill_data.verify_email

import android.view.LayoutInflater
import android.view.ViewGroup
import com.ghuljr.onehabit.databinding.FragmentVerifyEmailBinding
import com.ghuljr.onehabit.ui.base.BaseFragment
import com.ghuljr.onehabit_presenter.intro.fill_data.verify_email.VerifyEmailPresenter
import com.ghuljr.onehabit_presenter.intro.fill_data.verify_email.VerifyEmailView

class VerifyEmailFragment : BaseFragment<FragmentVerifyEmailBinding, VerifyEmailView, VerifyEmailPresenter>(), VerifyEmailView {

    override fun bindView(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentVerifyEmailBinding = FragmentVerifyEmailBinding.inflate(layoutInflater, container, false)

    override fun getPresenterView(): VerifyEmailView = this

}