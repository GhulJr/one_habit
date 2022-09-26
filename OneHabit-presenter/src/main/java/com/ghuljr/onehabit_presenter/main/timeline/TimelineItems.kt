package com.ghuljr.onehabit_presenter.main.timeline

import com.ghuljr.onehabit_tools.base.list.UniqueItem

sealed interface TimelineItem : UniqueItem

object HeaderItem : TimelineItem {

    override fun theSame(item: UniqueItem): Boolean = item is HeaderItem
    override fun matches(item: UniqueItem): Boolean = this == item
}

/*TODO: use ids to distinct items?*/
data class GoalItem(
    private val id: String,
    val dayNumber: Int,
    val state: State
) : TimelineItem {

    override fun theSame(item: UniqueItem): Boolean = id == (item as? GoalItem)?.id
    override fun matches(item: UniqueItem): Boolean = this == item

    sealed interface State {
        object Today : State
        object Future : State

        sealed interface Past : State {
            object Success : Past
            object Failure : Past
            object Partially : Past
        }
    }
}

data class SummaryItem(
    val dayNumber: Int,
    val totalDays: Int
) : TimelineItem {

    val percentage: Int = (dayNumber.toFloat() / totalDays * 100).toInt()

    override fun theSame(item: UniqueItem): Boolean = item is SummaryItem
    override fun matches(item: UniqueItem): Boolean = this == item
}