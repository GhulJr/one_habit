package com.ghuljr.onehabit.ui.add_action.items

import android.view.View
import com.ghuljr.onehabit.R
import com.ghuljr.onehabit.databinding.ItemReminderBinding
import com.ghuljr.onehabit_presenter.add_action.ReminderItem
import com.ghuljr.onehabit_tools.base.list.UniqueItem
import com.ghuljr.onehabit_tools_android.base.list.ViewHolderManager

class ReminderItemViewHolderManager : ViewHolderManager(R.layout.item_reminder) {

    override fun isType(item: UniqueItem): Boolean = item is ReminderItem


    override fun createViewHolder(view: View): ViewHolder<*> = object : ViewHolder<ReminderItem>(view) {

        private val viewBind = ItemReminderBinding.bind(view)

        override fun bind(item: ReminderItem) {
            viewBind.apply {
                reminder.text = item.timeDisplay
                remove.setOnClickListener { item.click() }
            }
        }
    }
}