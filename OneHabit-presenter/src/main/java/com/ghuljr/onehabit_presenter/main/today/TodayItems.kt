package com.ghuljr.onehabit_presenter.main.today

import com.ghuljr.onehabit_tools.base.list.UniqueItem
import java.sql.Timestamp

sealed interface TodayItem : UniqueItem {

    sealed class Action : TodayItem {
        abstract val title: String
        abstract val time: String?
        abstract val quantity: Quantity?
        protected abstract val onActionClick: () -> Unit
    }
}

/*TODO: use ids to distinct items?*/
data class TodayActionItem(
    override val title: String,
    override val time: String?,
    override val quantity: Quantity?,
    override val onActionClick: () -> Unit
) : TodayItem.Action() {

    override fun theSame(item: UniqueItem): Boolean = compareTo(item)
    override fun matches(item: UniqueItem): Boolean = compareTo(item)

    private fun compareTo(item: UniqueItem) = (item as? TodayActionItem)?.let {
        it.title == title && it.time == time && it.quantity?.first == quantity?.first
    } ?: false

    fun openActionDetails() = onActionClick()
}

data class CustomActionItem(
    override val title: String,
    override val time: String?,
    override val onActionClick: () -> Unit
) : TodayItem.Action() {

    override val quantity: Quantity? = null

    override fun theSame(item: UniqueItem): Boolean = compareTo(item)
    override fun matches(item: UniqueItem): Boolean = compareTo(item)

    private fun compareTo(item: UniqueItem) = (item as? CustomActionItem)?.let {
        it.title == title && it.time == time
    } ?: false

    fun openActionDetails() = onActionClick()
}

data class FinishedItem(
    override val title: String,
    override val time: String?,
    override val quantity: Quantity?,
    override val onActionClick: () -> Unit
) : TodayItem.Action() {

    override fun theSame(item: UniqueItem): Boolean = compareTo(item)
    override fun matches(item: UniqueItem): Boolean = compareTo(item)

    private fun compareTo(item: UniqueItem) = (item as? FinishedItem)?.let {
        it.title == title && it.time == time && it.quantity?.first == quantity?.first
    } ?: false

    fun openActionDetails() = onActionClick()
}

data class TodayHeaderItem(val title: String) : TodayItem {
    override fun theSame(item: UniqueItem): Boolean = (item as? TodayHeaderItem)?.title == title
    override fun matches(item: UniqueItem): Boolean = item == this
}

data class TodayTimestampItem(val timestamp: String) : TodayItem {
    override fun theSame(item: UniqueItem): Boolean = (item as? TodayTimestampItem)?.timestamp == timestamp
    override fun matches(item: UniqueItem): Boolean = item == this
}

object AddAction : TodayItem {
    override fun theSame(item: UniqueItem): Boolean = item is AddAction
    override fun matches(item: UniqueItem): Boolean = item == this
}

typealias Quantity = Pair<Int, Int>

