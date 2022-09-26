package com.ghuljr.onehabit.ui.main.today.list

import android.graphics.Paint
import android.view.View
import androidx.core.view.isVisible
import com.ghuljr.onehabit.R
import com.ghuljr.onehabit.databinding.ItemTodayFinishedBinding
import com.ghuljr.onehabit_presenter.main.today.TodayActionFinishedItem
import com.ghuljr.onehabit_tools.base.list.UniqueItem
import com.ghuljr.onehabit_tools_android.base.list.ViewHolderManager

class TodayActionFinishedViewHolderManager : ViewHolderManager(R.layout.item_today_finished) {

    override fun isType(item: UniqueItem): Boolean = item is TodayActionFinishedItem
    override fun createViewHolder(view: View): ViewHolder<*> =
        object : ViewHolder<TodayActionFinishedItem>(view) {

            private val viewBind = ItemTodayFinishedBinding.bind(view)

            override fun bind(item: TodayActionFinishedItem) {
                viewBind.apply {
                    title.apply {
                        paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                        title.text = item.customTitle ?: item.habitTopic.generateTitle(view.resources, item.habitSubject)
                    }
                    time.apply {
                        paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                        text = item.time ?: view.resources.getString(R.string.today)
                    }
                    quantity.apply {
                        paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                        isVisible = item.quantity != null
                        item.quantity?.let { text = view.resources.getString(R.string.today_quantity, it.first.toString(), it.second.toString()) }
                    }

                    root.setOnClickListener { item.openActionDetails() }
                }
            }

        }
}