package com.ghuljr.onehabit.ui.main

import android.content.Context
import android.content.Intent
import arrow.core.Nel
import com.ghuljr.onehabit.databinding.ActivityMainBinding
import com.ghuljr.onehabit.ui.base.BaseActivity
import com.ghuljr.onehabit.ui.intro.fill_data.FillUserDataActivity
import com.ghuljr.onehabit_presenter.main.FillRemainingDataSteps
import com.ghuljr.onehabit_presenter.main.MainPresenter
import com.ghuljr.onehabit_presenter.main.MainView

class MainActivity : BaseActivity<ActivityMainBinding, MainView, MainPresenter>(), MainView {

    override fun getPresenterView(): MainView = this
    override fun bindView(): ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)

    override fun redirectToFillRemainingData(missingData: Nel<FillRemainingDataSteps>) {
        startActivity(FillUserDataActivity.newIntent(this, missingData))
        finishAffinity()
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, MainActivity::class.java)
    }
}