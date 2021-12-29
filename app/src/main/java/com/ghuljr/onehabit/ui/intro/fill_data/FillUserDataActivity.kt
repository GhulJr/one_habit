package com.ghuljr.onehabit.ui.intro.fill_data

import android.content.Context
import android.content.Intent
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.ghuljr.onehabit.R
import com.ghuljr.onehabit.databinding.ActivityFillUserDataBinding
import com.ghuljr.onehabit.ui.base.BaseActivity
import com.ghuljr.onehabit.ui.intro.fill_data.verify_email.VerifyEmailFragmentDirections
import com.ghuljr.onehabit_presenter.intro.fill_data.FillUserDataPresenter
import com.ghuljr.onehabit_presenter.intro.fill_data.FillUserDataView
import com.ghuljr.onehabit_tools_android.extension.onDestinationChangedObservable
import com.ghuljr.onehabit_tools_android.extension.throttleClicks
import com.ghuljr.onehabit_tools_android.extension.throttleNavigationClicks
import com.jakewharton.rxbinding4.appcompat.navigationClicks
import io.reactivex.rxjava3.core.Observable
import java.lang.RuntimeException

class FillUserDataActivity : BaseActivity<ActivityFillUserDataBinding, FillUserDataView, FillUserDataPresenter>(), FillUserDataView {

    private val navController: NavController by lazy { findNavController(R.id.nav_host) }

    override fun onBackPressed() {
        presenter.navigateBackClicked()
    }

    override fun bindView(): ActivityFillUserDataBinding = ActivityFillUserDataBinding.inflate(layoutInflater)
    override fun getPresenterView(): FillUserDataView = this

    override fun signOutClickObservable(): Observable<Unit> = viewBind.signOutButton.throttleClicks()
    override fun navigateBackClickObservable(): Observable<Unit> = viewBind.toolbar.throttleNavigationClicks()

    override fun currentStepObservable(): Observable<FillUserDataView.CurrentStep> = navController.onDestinationChangedObservable {
        when(it.id) {
            R.id.verifyEmailFragment  -> FillUserDataView.CurrentStep.VERIFY_EMAIL
            R.id.verifyEmailFinishedFragment  -> FillUserDataView.CurrentStep.EMAIL_VERIFIED
            R.id.usernameFragment  -> FillUserDataView.CurrentStep.SET_DISPLAY_NAME
            else -> throw RuntimeException("Invalid destination = ${it.id}")
        }
    }

    override fun navigateBack() {
        if(!navController.popBackStack())
            finishAffinity()
    }

    override fun displayCounter(currentStepNumber: Int) {
        viewBind.stepsCounter.currentValue = currentStepNumber
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, FillUserDataActivity::class.java)
    }
}