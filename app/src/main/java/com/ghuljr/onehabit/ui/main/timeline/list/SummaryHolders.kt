package com.ghuljr.onehabit.ui.main.timeline.list

import android.view.View
import com.ghuljr.onehabit.R
import com.ghuljr.onehabit.databinding.ItemTimelineHeaderBinding
import com.ghuljr.onehabit_presenter.main.timeline.HeaderItem
import com.ghuljr.onehabit_presenter.main.timeline.SummaryItem
import com.ghuljr.onehabit_tools_android.base.list.UniqueItem
import com.ghuljr.onehabit_tools_android.base.list.ViewHolder
import com.ghuljr.onehabit_tools_android.base.list.ViewHolderManager

class SummaryViewHolderManager : ViewHolderManager<SummaryItem>(R.layout.item_timeline_summary) {

    override fun isType(item: UniqueItem): Boolean = item is HeaderItem
    override fun createViewHolder(view: View): ViewHolder<SummaryItem> = SummaryViewHolder(view)

}

class SummaryViewHolder(view: View) : ViewHolder<SummaryItem>(view) {

    private val viewBind = ItemTimelineHeaderBinding.bind(view)

    override fun bind(item: SummaryItem) {
        // TODO: handle binding
    }
}