package com.ghuljr.onehabit.ui.milestone_details

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.commit
import com.ghuljr.onehabit.R
import com.ghuljr.onehabit.databinding.ActivityMilestoneDetailsBinding
import com.ghuljr.onehabit.ui.base.BaseActivity
import com.ghuljr.onehabit.ui.main.timeline.TimelineFragment
import com.ghuljr.onehabit.ui.main.today.ActionsFragment
import com.ghuljr.onehabit_presenter.milestone_details.MilestoneDetailsPresenter
import com.ghuljr.onehabit_presenter.milestone_details.MilestoneDetailsView

class MilestoneDetailsActivity :
    BaseActivity<ActivityMilestoneDetailsBinding, MilestoneDetailsView, MilestoneDetailsPresenter>(),
    MilestoneDetailsView {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val milestoneId = intent.getStringExtra(EXTRA_MILESTONE_ID)!!
        val orderNumber = intent.getIntExtra(EXTRA_ORDER_NUMBER, 0)

        supportFragmentManager.commit {
            setReorderingAllowed(true)
            add(R.id.fragment_container, TimelineFragment.instance(milestoneId))
        }

        viewBind.apply {
            toolbar.apply {
                title = getString(R.string.week_param, orderNumber.toString())
                setNavigationOnClickListener { onBackPressed() }
            }
        }
    }

    override fun bindView(): ActivityMilestoneDetailsBinding =
        ActivityMilestoneDetailsBinding.inflate(layoutInflater)

    override fun getPresenterView(): MilestoneDetailsView = this

    companion object {
        private const val EXTRA_MILESTONE_ID = "extra_milestone_id"
        private const val EXTRA_ORDER_NUMBER = "extra_order_number"

        fun intent(from: Context, milestoneId: String, orderNumber: Int) =
            Intent(from, MilestoneDetailsActivity::class.java).apply {
                putExtra(EXTRA_MILESTONE_ID, milestoneId)
                putExtra(EXTRA_ORDER_NUMBER, orderNumber)
            }
    }
}