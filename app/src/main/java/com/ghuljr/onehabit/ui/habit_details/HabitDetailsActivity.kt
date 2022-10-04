package com.ghuljr.onehabit.ui.habit_details

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.ghuljr.onehabit.databinding.ActivityHabitDetailsBinding
import com.ghuljr.onehabit.ui.base.BaseActivity
import com.ghuljr.onehabit_presenter.habit_details.HabitDetailsPresenter
import com.ghuljr.onehabit_presenter.habit_details.HabitDetailsView

class HabitDetailsActivity : BaseActivity<ActivityHabitDetailsBinding, HabitDetailsView, HabitDetailsPresenter>(), HabitDetailsView {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun bindView(): ActivityHabitDetailsBinding = ActivityHabitDetailsBinding.inflate(layoutInflater)
    override fun getPresenterView(): HabitDetailsView = this

    companion object {

        private const val EXTRA_HABIT_ID = "extra_habit_id"

        fun intent(from: Context, habitId: String) = Intent(from, HabitDetailsActivity::class.java).apply {
            putExtra(EXTRA_HABIT_ID, habitId)
        }
    }
}