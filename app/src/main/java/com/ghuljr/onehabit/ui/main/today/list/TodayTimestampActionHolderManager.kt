package com.ghuljr.onehabit.ui.main.today.list

import android.view.View
import com.ghuljr.onehabit.R
import com.ghuljr.onehabit.databinding.ItemTodayActionTimestampBinding
import com.ghuljr.onehabit_presenter.main.today.TodayTimestampItem
import com.ghuljr.onehabit_tools.base.list.UniqueItem
import com.ghuljr.onehabit_tools_android.base.list.ViewHolderManager

class TodayTimestampActionHolderManager : ViewHolderManager(R.layout.item_today_action_timestamp) {

    override fun isType(item: UniqueItem): Boolean = item is TodayTimestampItem
    override fun createViewHolder(view: View): ViewHolder<*> = object : ViewHolder<TodayTimestampItem>(view) {

        private val viewBind = ItemTodayActionTimestampBinding.bind(view)

        override fun bind(item: TodayTimestampItem) {
            viewBind.root.text = item.timestamp
        }

    }
}