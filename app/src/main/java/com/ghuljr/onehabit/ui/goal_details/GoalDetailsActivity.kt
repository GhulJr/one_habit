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

class GoalDetailsActivity : BaseActivity<ActivityGoalDetailsBinding, GoalDetailsView, GoalDetailsPresenter>(),
    GoalDetailsView {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val goalId = intent.getStringExtra(EXTRA_GOAL_ID)!!
        val orderNumber = intent.getIntExtra(EXTRA_ORDER_NUMBER, 0)
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            add(R.id.fragment_container, ActionsFragment.instance(goalId))
        }

        viewBind.apply {
            toolbar.apply {
                title = getString(R.string.day_header, orderNumber.toString())
                setNavigationOnClickListener { onBackPressed() }
            }
        }
    }

    override fun bindView(): ActivityGoalDetailsBinding =
        ActivityGoalDetailsBinding.inflate(layoutInflater)

    override fun getPresenterView(): GoalDetailsView = this

    companion object {
        private const val EXTRA_GOAL_ID = "extra_goal_id"
        private const val EXTRA_ORDER_NUMBER = "extra_order_number"

        fun intent(from: Context, goalId: String, orderNumber: Int) =
            Intent(from, GoalDetailsActivity::class.java).apply {
                putExtra(EXTRA_GOAL_ID, goalId)
                putExtra(EXTRA_ORDER_NUMBER, orderNumber)
            }
    }
}