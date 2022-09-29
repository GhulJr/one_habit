package com.ghuljr.onehabit_presenter.add_action

import com.ghuljr.onehabit_tools.base.list.UniqueItem
import com.ghuljr.onehabit_tools.extension.timeToString

sealed interface BaseReminderItem : UniqueItem

data class ReminderItem(
    val time: Long,
    private val removeClick: () -> Unit
) : BaseReminderItem {

    val timeDisplay = time.timeToString(TIME_FORMAT)

    override fun theSame(item: UniqueItem): Boolean = (item as? ReminderItem)?.time == time
    override fun matches(item: UniqueItem): Boolean = item == this

    fun click() = removeClick()

    companion object {
        private const val TIME_FORMAT = "HH:mm"
    }
}

data class AddReminderItem(
    private val addClick: () -> Unit
) : BaseReminderItem {
    override fun theSame(item: UniqueItem): Boolean = item is AddReminderItem
    override fun matches(item: UniqueItem): Boolean = item == this

    fun click() = addClick()
}