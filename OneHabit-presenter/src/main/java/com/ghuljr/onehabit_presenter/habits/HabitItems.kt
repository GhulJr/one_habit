package com.ghuljr.onehabit_presenter.habits

import com.ghuljr.onehabit_tools.base.list.UniqueItem
import com.ghuljr.onehabit_tools.model.HabitTopic

data class HabitItem(
    val id: String,
    val habitTopic: HabitTopic,
    val habitSubject: String,
    private val onClick: () -> Unit
) : UniqueItem {

    override fun theSame(item: UniqueItem): Boolean = (item as? HabitItem)?.id == id
    override fun matches(item: UniqueItem): Boolean = item == this

    fun click() = onClick()
}