package com.ghuljr.onehabit.ui.add_action.items

import com.ghuljr.onehabit_tools.base.list.UniqueItem

data class ReminderItem(
    val time: String,
    private val removeClick: () -> Unit
) : UniqueItem {
    override fun theSame(item: UniqueItem): Boolean = (item as? ReminderItem)?.time == time
    override fun matches(item: UniqueItem): Boolean = item == this

    fun click() = removeClick()
}

data class AddReminderItem(
    private val addClick: () -> Unit
) : UniqueItem {
    override fun theSame(item: UniqueItem): Boolean = item is AddReminderItem
    override fun matches(item: UniqueItem): Boolean = item == this

    fun click() = addClick()
}