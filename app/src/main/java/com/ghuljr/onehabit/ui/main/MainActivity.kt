package com.ghuljr.onehabit.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.ghuljr.onehabit.R
import com.ghuljr.onehabit.databinding.ActivityMainBinding
import com.ghuljr.onehabit.ui.base.BaseActivity
import com.ghuljr.onehabit.ui.intro.change_data.FillUserDataActivity
import com.ghuljr.onehabit_presenter.main.MainPresenter
import com.ghuljr.onehabit_presenter.main.MainView

class MainActivity : BaseActivity<ActivityMainBinding, MainView, MainPresenter>(), MainView {

    override fun getPresenterView(): MainView = this
    override fun bindView(): ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)

    private val navController by lazy { (supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment).navController }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBind.bottomNavigation.setupWithNavController(navController)
    }

    override fun redirectToFillRemainingData() {
        startActivity(FillUserDataActivity.newIntent(this))
        finishAffinity()
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, MainActivity::class.java)
    }
}