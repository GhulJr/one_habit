package com.ghuljr.onehabit.ui.main.timeline.list

import android.view.View
import com.ghuljr.onehabit.R
import com.ghuljr.onehabit.databinding.ItemTimelineHeaderBinding
import com.ghuljr.onehabit_tools.base.list.UniqueItem
import com.ghuljr.onehabit_presenter.main.timeline.HeaderItem
import com.ghuljr.onehabit_tools_android.base.list.ViewHolderManager

class HeaderViewHolderManager : ViewHolderManager(R.layout.item_timeline_header) {

    override fun isType(item: UniqueItem): Boolean = item is HeaderItem

    override fun createViewHolder(view: View) = object : ViewHolder<HeaderItem>(view) {

        private val viewBind = ItemTimelineHeaderBinding.bind(view)

        override fun bind(item: HeaderItem) {
            // TODO: handle binding
        }
    }
}
