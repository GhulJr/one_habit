package com.ghuljr.onehabit.ui.intro.launch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import androidx.annotation.CallSuper
import androidx.viewbinding.ViewBinding
import com.ghuljr.onehabit.ui.base.BaseActivity
import com.ghuljr.onehabit.ui.intro.IntroActivity
import com.ghuljr.onehabit.ui.main.MainActivity
import com.ghuljr.onehabit_presenter.intro.launch.LaunchPresenter
import com.ghuljr.onehabit_presenter.intro.launch.LaunchView
import com.ghuljr.onehabit_tools_android.extension.asSingleTop
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

// Only activity without the view
class LaunchActivity : DaggerAppCompatActivity(), LaunchView {

    @Inject
    override lateinit var presenter: LaunchPresenter

    @CallSuper
    override fun onStart() {
        super.onStart()
        presenter.attach(this)
    }

    @CallSuper
    override fun onStop() {
        presenter.detach()
        super.onStop()
    }

    override fun userLoggedIn() {
        startActivity(MainActivity.newIntent(this).asSingleTop())
        finishAffinity()
    }

    override fun userLoggedOut() {
        startActivity(IntroActivity.newIntent(this).asSingleTop())
        finishAffinity()
    }
}