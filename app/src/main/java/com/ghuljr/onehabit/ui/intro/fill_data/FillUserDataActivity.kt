package com.ghuljr.onehabit.ui.intro.fill_data

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import arrow.core.Nel
import com.ghuljr.onehabit.R
import com.ghuljr.onehabit_presenter.main.FillRemainingDataSteps

class FillUserDataActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fill_data)
    }

    companion object {
        private const val EXTRA_IS_EMAIL_VERIFIED = "extra_is_email_verified"
        private const val EXTRA_IS_DISPLAY_NAME_SET = "extra_is_display_name_set"

        fun newIntent(context: Context, fillDataSteps: Nel<FillRemainingDataSteps>): Intent = Intent(context, FillUserDataActivity::class.java).apply {
            putExtra(EXTRA_IS_EMAIL_VERIFIED, fillDataSteps.contains(FillRemainingDataSteps.VERIFY_EMAIL))
            putExtra(EXTRA_IS_DISPLAY_NAME_SET, fillDataSteps.contains(FillRemainingDataSteps.SET_DISPLAY_NAME))
        }
    }
}