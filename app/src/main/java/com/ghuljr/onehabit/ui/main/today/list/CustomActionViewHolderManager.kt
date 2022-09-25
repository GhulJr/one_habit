package com.ghuljr.onehabit.ui.main.today.list

import android.view.View
import androidx.core.view.isVisible
import com.ghuljr.onehabit.R
import com.ghuljr.onehabit.databinding.ItemTodayActionCustomBinding
import com.ghuljr.onehabit.databinding.ItemTodayFinishedBinding
import com.ghuljr.onehabit_presenter.main.today.CustomActionItem
import com.ghuljr.onehabit_presenter.main.today.TodayActionFinishedItem
import com.ghuljr.onehabit_tools.base.list.UniqueItem
import com.ghuljr.onehabit_tools_android.base.list.ViewHolderManager

class CustomActionViewHolderManager : ViewHolderManager(R.layout.item_today_action_custom) {

    override fun isType(item: UniqueItem): Boolean = item is CustomActionItem

    override fun createViewHolder(view: View): ViewHolder<*> = object : ViewHolder<CustomActionItem>(view) {

        private val viewBind = ItemTodayActionCustomBinding.bind(view)

        override fun bind(item: CustomActionItem) {
            viewBind.apply {
                title.text = item.habitTopic.generateTitle(view.resources, item.habitSubject)
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