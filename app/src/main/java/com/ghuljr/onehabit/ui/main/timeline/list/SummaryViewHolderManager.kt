package com.ghuljr.onehabit.ui.main.timeline.list

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.ghuljr.onehabit.R
import com.ghuljr.onehabit.databinding.ItemTimelineHeaderBinding
import com.ghuljr.onehabit.databinding.ItemTimelineSummaryBinding
import com.ghuljr.onehabit_tools.base.list.UniqueItem
import com.ghuljr.onehabit_presenter.main.timeline.HeaderItem
import com.ghuljr.onehabit_presenter.main.timeline.SummaryItem
import com.ghuljr.onehabit_tools_android.base.list.ViewHolderManager

class SummaryViewHolderManager : ViewHolderManager(R.layout.item_timeline_summary) {

    override fun isType(item: UniqueItem): Boolean = item is SummaryItem

    override fun createViewHolder(view: View) = object : ViewHolder<SummaryItem>(view) {

        private val viewBind = ItemTimelineSummaryBinding.bind(view)

        override fun bind(item: SummaryItem) {
            viewBind.apply {
                header.text = view.resources.getString(R.string.summary)
                progress.text = view.resources.getString(R.string.percentage, item.percentage.toString())
            }
        }
    }
}