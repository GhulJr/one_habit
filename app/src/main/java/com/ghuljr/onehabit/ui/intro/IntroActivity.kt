package com.ghuljr.onehabit.ui.intro

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ghuljr.onehabit.databinding.ActivityIntroBinding
import com.ghuljr.onehabit.ui.base.BaseActivity
import com.ghuljr.onehabit.ui.intro.login.LoginActivity
import com.ghuljr.onehabit.ui.intro.register.RegisterActivity
import com.ghuljr.onehabit_presenter.intro.IntroPresenter
import com.ghuljr.onehabit_presenter.intro.IntroView
import com.ghuljr.onehabit_tools_android.extension.throttleClicks
import io.reactivex.rxjava3.core.Observable

class IntroActivity : BaseActivity<ActivityIntroBinding, IntroView, IntroPresenter>(), IntroView {

    override fun getPresenterView(): IntroView = this
    override fun bindView(): ActivityIntroBinding = ActivityIntroBinding.inflate(layoutInflater)

    override fun signInClickObservable(): Observable<Unit> = viewBind.signInButton.throttleClicks()
    override fun registerClickObservable(): Observable<Unit> = viewBind.registerButton.throttleClicks()

    override fun openSignIn(){
        startActivity(LoginActivity.newIntent(this))
    }

    override fun openRegister() {
        startActivity(RegisterActivity.newIntent(this))
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, IntroActivity::class.java)
    }
}