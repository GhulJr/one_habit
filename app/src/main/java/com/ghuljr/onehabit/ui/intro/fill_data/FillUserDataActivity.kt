package com.ghuljr.onehabit.ui.intro.fill_data

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import arrow.core.Nel
import com.ghuljr.onehabit.R
import com.ghuljr.onehabit.databinding.ActivityFillUserDataBinding
import com.ghuljr.onehabit.ui.base.BaseActivity
import com.ghuljr.onehabit_presenter.intro.fill_data.FillUserDataPresenter
import com.ghuljr.onehabit_presenter.intro.fill_data.FillUserDataView
import com.ghuljr.onehabit_presenter.main.FillRemainingDataSteps

class FillUserDataActivity : BaseActivity<ActivityFillUserDataBinding, FillUserDataView, FillUserDataPresenter>(), FillUserDataView {

    override fun bindView(): ActivityFillUserDataBinding = ActivityFillUserDataBinding.inflate(layoutInflater)
    override fun getPresenterView(): FillUserDataView = this

    companion object {
        const val EXTRA_IS_EMAIL_VERIFIED = "extra_is_email_verified"
        const val EXTRA_IS_DISPLAY_NAME_SET = "extra_is_display_name_set"

        fun newIntent(context: Context, fillDataSteps: Nel<FillRemainingDataSteps>): Intent = Intent(context, FillUserDataActivity::class.java).apply {
            putExtra(EXTRA_IS_EMAIL_VERIFIED, fillDataSteps.contains(FillRemainingDataSteps.VERIFY_EMAIL))
            putExtra(EXTRA_IS_DISPLAY_NAME_SET, fillDataSteps.contains(FillRemainingDataSteps.SET_DISPLAY_NAME))
        }
    }
}