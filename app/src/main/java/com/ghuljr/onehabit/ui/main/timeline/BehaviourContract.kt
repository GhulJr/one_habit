package com.ghuljr.onehabit.ui.main.timeline

import android.view.View
import com.ghuljr.onehabit.R
import com.ghuljr.onehabit.databinding.ItemTimelineBehaviourBinding
import com.ghuljr.onehabit_tools_android.base.list.UniqueItem
import com.ghuljr.onehabit_tools_android.base.list.ViewHolder
import com.ghuljr.onehabit_tools_android.base.list.ViewHolderManager

data class BehaviourItem(val title: String): TimelineItem {
    override fun theSame(item: UniqueItem): Boolean = title == (item as? BehaviourItem)?.title
    override fun matches(item: UniqueItem): Boolean = this == (item as? BehaviourItem)
}

class BehaviourViewHolderManager : ViewHolderManager<BehaviourItem>(R.layout.item_timeline_behaviour) {

    override fun isType(item: UniqueItem): Boolean = item is BehaviourItem
    override fun createViewHolder(view: View): ViewHolder<BehaviourItem> = BehaviourViewHolder(view)

    class BehaviourViewHolder(view: View) : ViewHolder<BehaviourItem>(view) {

        val viewBind = ItemTimelineBehaviourBinding.bind(view)

        override fun bind(item: BehaviourItem) {
            viewBind.title.text = item.title
        }
    }
}
