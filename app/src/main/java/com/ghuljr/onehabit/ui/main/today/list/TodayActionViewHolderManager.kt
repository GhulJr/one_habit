package com.ghuljr.onehabit.ui.main.today.list

import android.view.View
import androidx.core.view.isVisible
import com.ghuljr.onehabit.R
import com.ghuljr.onehabit.databinding.ItemTodayActionBinding
import com.ghuljr.onehabit_presenter.main.timeline.BehaviourItem
import com.ghuljr.onehabit_presenter.main.today.TodayActionItem
import com.ghuljr.onehabit_tools.base.list.UniqueItem
import com.ghuljr.onehabit_tools_android.base.list.ViewHolderManager

class TodayActionViewHolderManager : ViewHolderManager(R.layout.item_today_action) {

    override fun isType(item: UniqueItem): Boolean = item is TodayActionItem

    override fun createViewHolder(view: View): ViewHolder<*> = object : ViewHolder<TodayActionItem>(view) {

        private val viewBind = ItemTodayActionBinding.bind(view)

        override fun bind(item: TodayActionItem) {
            viewBind.apply {
                title.text = item.title
                time.text = item.time ?: view.resources.getString(R.string.today)
                quantity.apply {
                    isVisible = item.quantity != null
                    item.quantity?.let { text = view.resources.getString(R.string.today_quantity, it.first.toString(), it.second.toString()) }
                }

                root.setOnClickListener { item.openActionDetails() }
            }
        }

    }


}