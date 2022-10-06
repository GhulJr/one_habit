package com.ghuljr.onehabit.ui.habits

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ghuljr.onehabit.R
import com.ghuljr.onehabit.databinding.ActivityHabitsBinding
import com.ghuljr.onehabit.ui.base.BaseActivity
import com.ghuljr.onehabit_presenter.habits.HabitsPresenter
import com.ghuljr.onehabit_presenter.habits.HabitsView

class HabitsActivity : BaseActivity<ActivityHabitsBinding, HabitsView, HabitsPresenter>(), HabitsView {

    override fun bindView(): ActivityHabitsBinding = ActivityHabitsBinding.inflate(layoutInflater)
    override fun getPresenterView(): HabitsView = this

    companion object {

        fun intent(from: Context) = Intent(from, HabitsActivity::class.java)
    }
}