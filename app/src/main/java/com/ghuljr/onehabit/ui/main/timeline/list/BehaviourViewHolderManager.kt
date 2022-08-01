package com.ghuljr.onehabit.ui.main.timeline.list

import android.view.View
import com.ghuljr.onehabit.R
import com.ghuljr.onehabit.databinding.ItemTimelineBehaviourBinding
import com.ghuljr.onehabit_tools.base.list.UniqueItem
import com.ghuljr.onehabit_presenter.main.timeline.BehaviourItem
import com.ghuljr.onehabit_tools_android.base.list.ViewHolderManager

class BehaviourViewHolderManager : ViewHolderManager(R.layout.item_timeline_behaviour) {

    override fun isType(item: UniqueItem): Boolean = item is BehaviourItem

    override fun createViewHolder(view: View) = object : ViewHolder<BehaviourItem>(view) {

        private val viewBind = ItemTimelineBehaviourBinding.bind(view)

        override fun bind(item: BehaviourItem) {
            //viewBind.title.text = item.title
        }
    }
}