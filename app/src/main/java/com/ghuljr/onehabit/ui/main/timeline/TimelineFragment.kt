package com.ghuljr.onehabit.ui.main.timeline

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.ghuljr.onehabit.databinding.FragmentTimelineBinding
import com.ghuljr.onehabit.ui.base.BaseFragment
import com.ghuljr.onehabit.ui.main.MainActivity
import com.ghuljr.onehabit.ui.main.timeline.list.BehaviourViewHolderManager
import com.ghuljr.onehabit.ui.main.timeline.list.HeaderViewHolderManager
import com.ghuljr.onehabit.ui.main.timeline.list.SummaryViewHolderManager
import com.ghuljr.onehabit_presenter.main.MainStep
import com.ghuljr.onehabit_presenter.main.timeline.TimelineItem
import com.ghuljr.onehabit_presenter.main.timeline.TimelinePresenter
import com.ghuljr.onehabit_presenter.main.timeline.TimelineView
import com.ghuljr.onehabit_tools_android.base.list.ItemListAdapter


class TimelineFragment : BaseFragment<FragmentTimelineBinding, TimelineView, TimelinePresenter>(), TimelineView {

    private val timelineAdapter = ItemListAdapter(
        listOf(
            HeaderViewHolderManager(),
            BehaviourViewHolderManager(),
            SummaryViewHolderManager()
        )
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as? MainActivity)?.setCurrentStep(MainStep.TIMELINE)

        viewBind?.apply {
            timelineRecyclerView.apply {
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                adapter = timelineAdapter
            }
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
}