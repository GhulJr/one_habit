package com.ghuljr.onehabit.ui.main.today.list

import android.view.View
import com.ghuljr.onehabit.R
import com.ghuljr.onehabit.databinding.ItemTodayActionBinding
import com.ghuljr.onehabit_presenter.main.today.AddActionItem
import com.ghuljr.onehabit_tools.base.list.UniqueItem
import com.ghuljr.onehabit_tools_android.base.list.ViewHolderManager

class AddActionViewHolderManager : ViewHolderManager(R.layout.item_today_action_add) {

    override fun isType(item: UniqueItem): Boolean = item is AddActionItem

    override fun createViewHolder(view: View): ViewHolder<*> = object : ViewHolder<AddActionItem>(view) {

        private val viewBind = ItemTodayActionBinding.bind(view)

        override fun bind(item: AddActionItem) {
            viewBind.root.setOnClickListener { item.addAction() }
        }

    }

}