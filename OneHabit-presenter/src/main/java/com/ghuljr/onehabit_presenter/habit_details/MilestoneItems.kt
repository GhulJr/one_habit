package com.ghuljr.onehabit_presenter.habit_details

import com.ghuljr.onehabit_tools.base.list.UniqueItem

data class MilestoneItem(
    val id: String,
    val orderNumber: Int,
    private val onClick: () -> Unit
) : UniqueItem {

    override fun theSame(item: UniqueItem): Boolean = (item as? MilestoneItem)?.id == id
    override fun matches(item: UniqueItem): Boolean = item == this

    fun click() = onClick()
}