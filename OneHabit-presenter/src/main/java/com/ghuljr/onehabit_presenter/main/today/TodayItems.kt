package com.ghuljr.onehabit_presenter.main.today

import com.ghuljr.onehabit_tools.base.list.UniqueItem

sealed interface TodayItem : UniqueItem

/*TODO: use ids to distinct items?*/
data class TodayActionItem(
    val title: String,
    val time: String?,
    val quantity: Quantity?,
    private val onActionClick: () -> Unit
) : TodayItem {

    override fun theSame(item: UniqueItem): Boolean = compareTo(item)
    override fun matches(item: UniqueItem): Boolean = compareTo(item)

    private fun compareTo(item: UniqueItem) = (item as? TodayActionItem)?.let {
        it.title == title && it.time == time && it.quantity?.first == quantity?.first
    } ?: false

    fun openActionDetails() = onActionClick()
}

data class CustomAction(val w: String) : TodayItem {
    override fun theSame(item: UniqueItem): Boolean {
        TODO("Not yet implemented")
    }

    override fun matches(item: UniqueItem): Boolean {
        TODO("Not yet implemented")
    }
}

data class FinishedItem(val w: String) : TodayItem {
    override fun theSame(item: UniqueItem): Boolean {
        TODO("Not yet implemented")
    }

    override fun matches(item: UniqueItem): Boolean {
        TODO("Not yet implemented")
    }
}

data class TodayHeaderItem(val w: String) : TodayItem {
    override fun theSame(item: UniqueItem): Boolean {
        TODO("Not yet implemented")
    }

    override fun matches(item: UniqueItem): Boolean {
        TODO("Not yet implemented")
    }
}

data class TodayTimestampItem(val w: String) : TodayItem {
    override fun theSame(item: UniqueItem): Boolean {
        TODO("Not yet implemented")
    }

    override fun matches(item: UniqueItem): Boolean {
        TODO("Not yet implemented")
    }

}

object AddAction : TodayItem {
    override fun theSame(item: UniqueItem): Boolean = item is AddAction
    override fun matches(item: UniqueItem): Boolean = item == this
}

typealias Quantity = Pair<Int, Int>

