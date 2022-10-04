package com.ghuljr.onehabit.ui.goal_details

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.commit
import com.ghuljr.onehabit.R
import com.ghuljr.onehabit.databinding.ActivityGoalDetailsBinding
import com.ghuljr.onehabit.ui.base.BaseActivity
import com.ghuljr.onehabit.ui.main.today.ActionsFragment
import com.ghuljr.onehabit_presenter.goal_details.GoalDetailsPresenter
import com.ghuljr.onehabit_presenter.goal_details.GoalDetailsView

class GoalDetailsActivity : BaseActivity<ActivityGoalDetailsBinding, GoalDetailsView, GoalDetailsPresenter>(), GoalDetailsView {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val goalId = intent.getStringExtra(EXTRA_GOAL_ID)!!
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            add(R.id.fragment_container, ActionsFragment.instance(goalId))
        }

        presenter.init(goalId)

        viewBind.apply {
            toolbar.setNavigationOnClickListener { onBackPressed() }
        }
    }

    override fun bindView(): ActivityGoalDetailsBinding = ActivityGoalDetailsBinding.inflate(layoutInflater)
    override fun getPresenterView(): GoalDetailsView = this

    override fun displayDayNumber(dayNumber: Int) {
        viewBind.toolbar.title = getString(R.string.day_header, dayNumber.toString())
    }

    companion object {
        private const val EXTRA_GOAL_ID = "extra_goal_id"

        fun intent(from: Context, goalId: String) = Intent(from, GoalDetailsActivity::class.java).apply {
            putExtra(EXTRA_GOAL_ID, goalId)
        }
    }
}