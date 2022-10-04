package com.ghuljr.onehabit.ui.main.timeline

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import arrow.core.Option
import arrow.core.toOption
import com.ghuljr.onehabit.R
import com.ghuljr.onehabit.databinding.FragmentTimelineBinding
import com.ghuljr.onehabit.ui.base.BaseFragment
import com.ghuljr.onehabit.ui.goal_details.GoalDetailsActivity
import com.ghuljr.onehabit.ui.main.MainActivity
import com.ghuljr.onehabit.ui.main.timeline.list.GoalViewHolderManager
import com.ghuljr.onehabit.ui.main.timeline.list.HeaderViewHolderManager
import com.ghuljr.onehabit.ui.main.timeline.list.SummaryViewHolderManager
import com.ghuljr.onehabit.ui.main.today.ActionsFragment
import com.ghuljr.onehabit_error.BaseEvent
import com.ghuljr.onehabit_error_android.event_handler.EventHandler
import com.ghuljr.onehabit_error_android.event_manager.SnackbarEventManager
import com.ghuljr.onehabit_presenter.main.MainStep
import com.ghuljr.onehabit_presenter.main.timeline.TimelineItem
import com.ghuljr.onehabit_presenter.main.timeline.TimelinePresenter
import com.ghuljr.onehabit_presenter.main.timeline.TimelineView
import com.ghuljr.onehabit_tools_android.base.list.ItemListAdapter
import com.google.android.material.snackbar.Snackbar


class TimelineFragment : BaseFragment<FragmentTimelineBinding, TimelineView, TimelinePresenter>(),
    TimelineView {

    private val timelineAdapter = ItemListAdapter(
        HeaderViewHolderManager(),
        GoalViewHolderManager(),
        SummaryViewHolderManager()
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as? MainActivity)?.setCurrentStep(MainStep.TIMELINE)
        presenter.init(arguments?.getString(EXTRA_MILESTONE_ID).toOption())

        viewBind.apply {
            timelineRecyclerView.apply {
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                adapter = timelineAdapter
            }

            swipeRefresh.setOnRefreshListener { presenter.refresh() }
        }
    }

    override fun bindView(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTimelineBinding = FragmentTimelineBinding.inflate(layoutInflater, container, false)

    override fun getPresenterView(): TimelineView = this

    override fun submitItems(items: List<TimelineItem>) {
        timelineAdapter.submitList(items)
    }

    override fun handleEvent(event: Option<BaseEvent>) {
        viewBind.swipeRefresh.isRefreshing = false
        val eventManager = EventHandler(listOf(SnackbarEventManager(
            eventView = viewBind.root,
            duration = Snackbar.LENGTH_INDEFINITE,
            actionWithName = { } to getString(R.string.ok)
        )), viewBind.loadingIndicator)

        eventManager(event)
    }

    override fun openGoalDetails(goalId: String) {
        startActivity(GoalDetailsActivity.intent(requireContext(), goalId))
    }

    companion object {

        private const val EXTRA_MILESTONE_ID = "extra_milestone_id"

        fun instance(habitId: String?) = TimelineFragment().apply {
            arguments = Bundle().apply {
                putString(EXTRA_MILESTONE_ID, habitId)
            }
        }
    }
}