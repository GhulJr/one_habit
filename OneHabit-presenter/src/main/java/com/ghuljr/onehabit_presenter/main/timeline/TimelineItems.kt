package com.ghuljr.onehabit_presenter.main.timeline

import com.ghuljr.onehabit_tools.base.list.UniqueItem

sealed interface TimelineItem : UniqueItem

data class HeaderItem(val title: String) : TimelineItem {

    override fun theSame(item: UniqueItem): Boolean = title == (item as? HeaderItem)?.title
    override fun matches(item: UniqueItem): Boolean = this == item
}

data class BehaviourItem(val title: String): TimelineItem {
    override fun theSame(item: UniqueItem): Boolean = title == (item as? BehaviourItem)?.title
    override fun matches(item: UniqueItem): Boolean = this == item
}

data class SummaryItem(val title: String) : TimelineItem {

    override fun theSame(item: UniqueItem): Boolean = title == (item as? HeaderItem)?.title
    override fun matches(item: UniqueItem): Boolean = this == item
}