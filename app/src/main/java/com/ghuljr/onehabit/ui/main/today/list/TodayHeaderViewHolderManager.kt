package com.ghuljr.onehabit.ui.main.today.list

import android.view.View
import com.ghuljr.onehabit.R
import com.ghuljr.onehabit.databinding.ItemTodayActionHeaderBinding
import com.ghuljr.onehabit_presenter.main.today.TodayHeaderItem
import com.ghuljr.onehabit_tools.base.list.UniqueItem
import com.ghuljr.onehabit_tools_android.base.list.ViewHolderManager

class TodayHeaderViewHolderManager : ViewHolderManager(R.layout.item_today_action_header) {

    override fun isType(item: UniqueItem): Boolean = item is TodayHeaderItem

    override fun createViewHolder(view: View): ViewHolder<*> = object : ViewHolder<TodayHeaderItem>(view) {

        private val viewBind = ItemTodayActionHeaderBinding.bind(view)

        override fun bind(item: TodayHeaderItem) {
            viewBind.root.text = item.title
        }

    }
}