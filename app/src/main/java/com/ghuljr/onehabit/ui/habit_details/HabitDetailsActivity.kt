package com.ghuljr.onehabit.ui.habit_details

import android.content.Context
import android.content.Intent
import android.os.Bundle
import arrow.core.Option
import com.ghuljr.onehabit.R
import com.ghuljr.onehabit.databinding.ActivityHabitDetailsBinding
import com.ghuljr.onehabit.ui.base.BaseActivity
import com.ghuljr.onehabit.ui.main.today.list.generateTitle
import com.ghuljr.onehabit_error.BaseEvent
import com.ghuljr.onehabit_error_android.event_handler.EventHandler
import com.ghuljr.onehabit_error_android.event_manager.SnackbarEventManager
import com.ghuljr.onehabit_presenter.habit_details.HabitDetailsPresenter
import com.ghuljr.onehabit_presenter.habit_details.HabitDetailsView
import com.ghuljr.onehabit_tools.model.HabitTopic
import com.google.android.material.snackbar.Snackbar

class HabitDetailsActivity :
    BaseActivity<ActivityHabitDetailsBinding, HabitDetailsView, HabitDetailsPresenter>(),
    HabitDetailsView {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.init(intent.getStringExtra(EXTRA_HABIT_ID)!!)
        viewBind.apply {
            toolbar.setNavigationOnClickListener { onBackPressed() }
        }
    }

    override fun bindView(): ActivityHabitDetailsBinding =
        ActivityHabitDetailsBinding.inflate(layoutInflater)

    override fun getPresenterView(): HabitDetailsView = this

    override fun displayCurrentHabitData(
        habitTopic: HabitTopic,
        habitSubject: String,
        intensityProgress: Int
    ) {
        viewBind.apply {
            currentHabitTitle.text = habitTopic.generateTitle(resources, habitSubject)
            currentHabitPercentageProgress.text = "$intensityProgress%"
            currentHabitProgressBar.progress = intensityProgress
        }
    }

    override fun handleEvent(event: Option<BaseEvent>) {
        val eventHandler = EventHandler(listOf(SnackbarEventManager(
            eventView = viewBind.root,
            duration = Snackbar.LENGTH_INDEFINITE,
            actionWithName = { } to getString(R.string.ok)
        )), viewBind.loadingIndicator)

        eventHandler(event)
    }

    companion object {

        private const val EXTRA_HABIT_ID = "extra_habit_id"

        fun intent(from: Context, habitId: String) =
            Intent(from, HabitDetailsActivity::class.java).apply {
                putExtra(EXTRA_HABIT_ID, habitId)
            }
    }
}