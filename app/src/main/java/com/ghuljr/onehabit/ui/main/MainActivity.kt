package com.ghuljr.onehabit.ui.main

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.ghuljr.onehabit.R
import com.ghuljr.onehabit.databinding.ActivityMainBinding
import com.ghuljr.onehabit.ui.base.BaseActivity
import com.ghuljr.onehabit.ui.intro.change_data.FillUserDataActivity
import com.ghuljr.onehabit_presenter.main.MainPresenter
import com.ghuljr.onehabit_presenter.main.MainStep
import com.ghuljr.onehabit_presenter.main.MainView

class MainActivity : BaseActivity<ActivityMainBinding, MainView, MainPresenter>(), MainView {

    override fun getPresenterView(): MainView = this
    override fun bindView(): ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)

    private val navController by lazy { (supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment).navController }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBind.apply {
            bottomNavigation.setupWithNavController(navController)
        }
    }

    override fun redirectToFillRemainingData() {
        startActivity(FillUserDataActivity.newIntent(this))
        finishAffinity()
    }

    override fun changeCurrentStep(currentStep: MainStep) {
        setSubtitle("")
        setTitle(
            getString(
                when (currentStep) {
                    MainStep.TIMELINE -> R.string.timeline
                    MainStep.TODAY -> R.string.today
                    MainStep.PROFILE -> R.string.profile
                }
            )
        )
    }

    override fun setTitle(title: String) {
        viewBind.toolbar.title = title
    }

    override fun setSubtitle(subtitle: String) {
        viewBind.toolbar.subtitle = subtitle
    }

    override fun askForChoosingHabit() {
        AlertDialog.Builder(this)
            .setTitle(R.string.select_habit_title)
            .setMessage(R.string.select_habit_message)
            .setPositiveButton(R.string.ok) { _,_ -> /* TODO: open activity with the list of habits */ }
            .setCancelable(true)
            .show()
    }

    fun setCurrentStep(currentStep: MainStep) {
        presenter.setCurrentStep(currentStep)
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, MainActivity::class.java)
    }
}