package com.ghuljr.onehabit.ui.habit_details

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import arrow.core.Option
import com.ghuljr.onehabit.R
import com.ghuljr.onehabit.databinding.ActivityHabitDetailsBinding
import com.ghuljr.onehabit.ui.base.BaseActivity
import com.ghuljr.onehabit.ui.habit_details.list.MilestoneViewHolderManager
import com.ghuljr.onehabit.ui.main.today.list.generateTitle
import com.ghuljr.onehabit.ui.milestone_details.MilestoneDetailsActivity
import com.ghuljr.onehabit_error.BaseEvent
import com.ghuljr.onehabit_error_android.event_handler.EventHandler
import com.ghuljr.onehabit_error_android.event_manager.SnackbarEventManager
import com.ghuljr.onehabit_presenter.habit_details.HabitDetailsPresenter
import com.ghuljr.onehabit_presenter.habit_details.HabitDetailsView
import com.ghuljr.onehabit_presenter.habit_details.MilestoneItem
import com.ghuljr.onehabit_tools.model.HabitTopic
import com.ghuljr.onehabit_tools_android.base.list.ItemListAdapter
import com.google.android.material.snackbar.Snackbar

class HabitDetailsActivity : BaseActivity<ActivityHabitDetailsBinding, HabitDetailsView, HabitDetailsPresenter>(),
    HabitDetailsView {

    private val milestonesAdapter = ItemListAdapter(MilestoneViewHolderManager())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBind.apply {
            toolbar.setNavigationOnClickListener { onBackPressed() }
            milestonesRecyclerView.apply {
                layoutManager = LinearLayoutManager(this@HabitDetailsActivity, LinearLayoutManager.VERTICAL, false)
                adapter = milestonesAdapter
            }
        }
        presenter.init(intent.getStringExtra(EXTRA_HABIT_ID)!!)
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
            setAsCurrent.setOnClickListener { presenter.setAsCurrent() }
        }
    }

    override fun handleEvent(event: Option<BaseEvent>) {

    }

    override fun displayMilestoneItems(items: List<MilestoneItem>) {
        milestonesAdapter.submitList(items)
    }

    override fun close() {
        onBackPressed()
    }

    override fun openMilestoneDetails(milestoneId: String, orderNumber: Int) {
        startActivity(MilestoneDetailsActivity.intent(this, milestoneId, orderNumber))
    }

    override fun displaySetAsCurrent(display: Boolean) {
       viewBind.setAsCurrent.isVisible = display
    }

    companion object {

        private const val EXTRA_HABIT_ID = "extra_habit_id"

        fun intent(from: Context, habitId: String) =
            Intent(from, HabitDetailsActivity::class.java).apply {
                putExtra(EXTRA_HABIT_ID, habitId)
            }
    }
}