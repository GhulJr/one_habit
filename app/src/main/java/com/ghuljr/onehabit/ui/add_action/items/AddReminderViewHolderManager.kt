package com.ghuljr.onehabit.ui.add_action.items

import android.view.View
import com.ghuljr.onehabit.R
import com.ghuljr.onehabit.databinding.ItemReminderAddBinding
import com.ghuljr.onehabit_tools.base.list.UniqueItem
import com.ghuljr.onehabit_tools_android.base.list.ViewHolderManager

class AddReminderViewHolderManager : ViewHolderManager(R.layout.item_reminder_add) {

    override fun isType(item: UniqueItem): Boolean = item is AddReminderItem

    override fun createViewHolder(view: View): ViewHolder<*> =
        object : ViewHolder<AddReminderItem>(view) {

            private val viewBind = ItemReminderAddBinding.bind(view)

            override fun bind(item: AddReminderItem) {
                viewBind.root.setOnClickListener { item.click() }
            }
        }
}