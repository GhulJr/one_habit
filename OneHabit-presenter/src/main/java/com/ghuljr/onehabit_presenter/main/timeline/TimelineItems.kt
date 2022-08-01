package com.ghuljr.onehabit_presenter.main.timeline

import com.ghuljr.onehabit_tools.base.list.UniqueItem

sealed interface TimelineItem : UniqueItem

data class HeaderItem(val title: String) : TimelineItem {

    override fun theSame(item: UniqueItem): Boolean = title == (item as? HeaderItem)?.title
    override fun matches(item: UniqueItem): Boolean = this == item
}

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

data class SummaryItem(val title: String) : TimelineItem {

    override fun theSame(item: UniqueItem): Boolean = title == (item as? HeaderItem)?.title
    override fun matches(item: UniqueItem): Boolean = this == item
}