package com.ghuljr.onehabit.ui.habits

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import arrow.core.Option
import com.ghuljr.onehabit.R
import com.ghuljr.onehabit.databinding.ActivityHabitsBinding
import com.ghuljr.onehabit.ui.base.BaseActivity
import com.ghuljr.onehabit.ui.habit_details.HabitDetailsActivity
import com.ghuljr.onehabit_error.BaseEvent
import com.ghuljr.onehabit_error_android.event_handler.EventHandler
import com.ghuljr.onehabit_error_android.event_manager.SnackbarEventManager
import com.ghuljr.onehabit_presenter.habits.HabitItem
import com.ghuljr.onehabit_presenter.habits.HabitsPresenter
import com.ghuljr.onehabit_presenter.habits.HabitsView
import com.ghuljr.onehabit_tools_android.base.list.ItemListAdapter
import com.google.android.material.snackbar.Snackbar

class HabitsActivity : BaseActivity<ActivityHabitsBinding, HabitsView, HabitsPresenter>(), HabitsView {

    private val habitsAdapter = ItemListAdapter(HabitsViewHolderManager())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewBind.apply {
            toolbar.setNavigationOnClickListener { onBackPressed() }
            habitsRecyclerView.apply {
                layoutManager = LinearLayoutManager(this@HabitsActivity, LinearLayoutManager.VERTICAL, false)
                adapter = habitsAdapter
            }
        }
    }

    override fun bindView(): ActivityHabitsBinding = ActivityHabitsBinding.inflate(layoutInflater)
    override fun getPresenterView(): HabitsView = this

    override fun submitList(habits: List<HabitItem>) {
        habitsAdapter.submitList(habits)
    }

    override fun handleEvent(event: Option<BaseEvent>) {
        val eventHandler = EventHandler(listOf(SnackbarEventManager(
            eventView = viewBind.root,
            duration = Snackbar.LENGTH_INDEFINITE,
            actionWithName = { } to getString(R.string.ok)
        )), viewBind.loadingIndicator)
        eventHandler(event)
    }

    override fun openHabitDetails(habitId: String) {
        startActivity(HabitDetailsActivity.intent(this, habitId))
    }

    companion object {

        fun intent(from: Context) = Intent(from, HabitsActivity::class.java)
    }
}