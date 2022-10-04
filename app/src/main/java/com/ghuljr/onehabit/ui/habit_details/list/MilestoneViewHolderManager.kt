package com.ghuljr.onehabit.ui.habit_details.list

import android.view.View
import com.ghuljr.onehabit.R
import com.ghuljr.onehabit.databinding.ItemMilestoneBinding
import com.ghuljr.onehabit_presenter.habit_details.MilestoneItem
import com.ghuljr.onehabit_tools.base.list.UniqueItem
import com.ghuljr.onehabit_tools_android.base.list.ViewHolderManager

class MilestoneViewHolderManager : ViewHolderManager(R.layout.item_milestone) {

    override fun isType(item: UniqueItem): Boolean = item is MilestoneItem

    override fun createViewHolder(view: View): ViewHolder<*> = object : ViewHolder<MilestoneItem>(view) {

        private val viewBind = ItemMilestoneBinding.bind(view)

        override fun bind(item: MilestoneItem) {
            viewBind.apply {
                milestoneItemTitle.text = view.resources.getString(R.string.week_param, item.orderNumber.toString())
                root.setOnClickListener { item.click() }
            }
        }
    }
}