package com.ghuljr.onehabit.ui.main.today.list

import android.content.res.Resources
import android.view.View
import androidx.core.view.isVisible
import com.ghuljr.onehabit.R
import com.ghuljr.onehabit.databinding.ItemTodayActionBinding
import com.ghuljr.onehabit_presenter.main.today.TodayActionItem
import com.ghuljr.onehabit_tools.base.list.UniqueItem
import com.ghuljr.onehabit_tools.model.HabitTopic
import com.ghuljr.onehabit_tools_android.base.list.ViewHolderManager
import kotlin.math.abs

class TodayActionViewHolderManager : ViewHolderManager(R.layout.item_today_action) {

    override fun isType(item: UniqueItem): Boolean = item is TodayActionItem

    override fun createViewHolder(view: View): ViewHolder<*> =
        object : ViewHolder<TodayActionItem>(view) {

            private val viewBind = ItemTodayActionBinding.bind(view)

            override fun bind(item: TodayActionItem) {
                viewBind.apply {
                    title.text = view.resources.generateTitle(item.habitTopic, item.habitSubject, item.exceeded)
                    time.text = item.time ?: when(item.type) {
                        TodayActionItem.Type.DAILY -> view.resources.getString(R.string.today)
                        TodayActionItem.Type.WEEKLY -> view.resources.getString(R.string.week)
                    }
                    quantity.apply {
                        isVisible = item.quantity != null

                        item.quantity?.let {
                            text = when (item.type) {
                                    TodayActionItem.Type.DAILY -> view.resources.getString(R.string.today_quantity, it.first.toString(), it.second.toString())
                                    TodayActionItem.Type.WEEKLY -> view.resources.getString(
                                        if (item.exceeded) R.string.today_quantity_exceeded
                                        else R.string.today_quantity_remaining,
                                        abs(it.second - it.first).toString()
                                    )
                                }
                        }
                    }

                    root.setOnClickListener { item.openActionDetails() }
                }
            }
        }
}