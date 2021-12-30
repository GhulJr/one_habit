package com.ghuljr.onehabit.ui.intro.login.forgot_password

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ghuljr.onehabit.R
import com.ghuljr.onehabit.databinding.ActivityForgotPasswordBinding
import com.ghuljr.onehabit.ui.base.BaseActivity
import com.ghuljr.onehabit_presenter.intro.login.forgot_password.ForgotPasswordPresenter
import com.ghuljr.onehabit_presenter.intro.login.forgot_password.ForgotPasswordView

class ForgotPasswordActivity : BaseActivity<ActivityForgotPasswordBinding, ForgotPasswordView, ForgotPasswordPresenter>(), ForgotPasswordView {

    override fun bindView(): ActivityForgotPasswordBinding = ActivityForgotPasswordBinding.inflate(layoutInflater)
    override fun getPresenterView(): ForgotPasswordView = this

}