package com.ghuljr.onehabit_presenter.main.timeline

import com.ghuljr.onehabit_tools.base.list.UniqueItem

sealed interface TimelineItem : UniqueItem

data class HeaderItem(val title: String) : TimelineItem {

    override fun theSame(item: UniqueItem): Boolean = item is HeaderItem
    override fun matches(item: UniqueItem): Boolean = this == item
}

/*TODO: use ids to distinct items?*/
data class BehaviourItem(
    val title: String,
    val dayNumber: Int,
    val state: State
) : TimelineItem {

    override fun theSame(item: UniqueItem): Boolean = dayNumber == (item as? BehaviourItem)?.dayNumber
    override fun matches(item: UniqueItem): Boolean = this == item

    sealed interface State {
        object Today : State
        object Future : State

        sealed interface Past : State {
            object Success : Past
            object Failure : Past
            object NotSubmitted : Past
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