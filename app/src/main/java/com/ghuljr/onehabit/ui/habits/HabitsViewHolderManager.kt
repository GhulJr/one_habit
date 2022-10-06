package com.ghuljr.onehabit.ui.habits

import android.view.View
import com.ghuljr.onehabit.R
import com.ghuljr.onehabit.databinding.ItemHabitBinding
import com.ghuljr.onehabit.ui.main.today.list.generateTitle
import com.ghuljr.onehabit_presenter.habits.HabitItem
import com.ghuljr.onehabit_tools.base.list.UniqueItem
import com.ghuljr.onehabit_tools_android.base.list.ViewHolderManager

class HabitsViewHolderManager : ViewHolderManager(R.layout.item_habit) {

    override fun isType(item: UniqueItem): Boolean = item is HabitItem

    override fun createViewHolder(view: View): ViewHolder<*> = object : ViewHolder<HabitItem>(view) {

        private val viewBind = ItemHabitBinding.bind(view)

        override fun bind(item: HabitItem) {
           viewBind.title.apply {
               text = item.run { habitTopic.generateTitle(resources, habitSubject) }
               setOnClickListener { item.click() }
           }
        }
    }
}