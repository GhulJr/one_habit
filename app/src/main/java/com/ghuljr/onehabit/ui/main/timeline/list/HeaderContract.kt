package com.ghuljr.onehabit.ui.main.timeline.list

import android.view.View
import com.ghuljr.onehabit.R
import com.ghuljr.onehabit.databinding.ItemTimelineHeaderBinding
import com.ghuljr.onehabit_tools_android.base.list.UniqueItem
import com.ghuljr.onehabit_tools_android.base.list.ViewHolder
import com.ghuljr.onehabit_tools_android.base.list.ViewHolderManager

data class HeaderItem(val title: String) : UniqueItem {

    override fun theSame(item: UniqueItem): Boolean = title == (item as? HeaderItem)?.title
    override fun matches(item: UniqueItem): Boolean = this == item
}

class HeaderViewHolderManager : ViewHolderManager<HeaderItem>(R.layout.item_timeline_header) {

    override fun isType(item: UniqueItem): Boolean = item is HeaderItem
    override fun createViewHolder(view: View): ViewHolder<HeaderItem> = HeaderViewHolder(view)

}

class HeaderViewHolder(view: View) : ViewHolder<HeaderItem>(view) {

    private val viewBind = ItemTimelineHeaderBinding.bind(view)

    override fun bind(item: HeaderItem) {
        // TODO: handle binding
    }
}
