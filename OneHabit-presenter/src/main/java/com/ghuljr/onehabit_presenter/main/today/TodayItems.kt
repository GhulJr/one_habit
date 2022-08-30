package com.ghuljr.onehabit_presenter.main.today

import com.ghuljr.onehabit_tools.base.list.UniqueItem

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

data class TodayActionFinishedItem(
    override val title: String,
    override val time: String?,
    override val quantity: Quantity?,
    override val onActionClick: () -> Unit
) : TodayItem.Action() {

    override fun theSame(item: UniqueItem): Boolean = compareTo(item)
    override fun matches(item: UniqueItem): Boolean = compareTo(item)

    private fun compareTo(item: UniqueItem) = (item as? TodayActionFinishedItem)?.let {
        it.title == title && it.time == time && it.quantity?.first == quantity?.first
    } ?: false

    fun openActionDetails() = onActionClick()
}

object DoneActionsHeaderItem : TodayItem {
    override fun theSame(item: UniqueItem): Boolean = item is DoneActionsHeaderItem
    override fun matches(item: UniqueItem): Boolean = item == this
}

data class AddActionItem(
    private val onActionClick: () -> Unit
) : TodayItem {
    override fun theSame(item: UniqueItem): Boolean = item is AddActionItem
    override fun matches(item: UniqueItem): Boolean = item == this

    fun addAction() = onActionClick()
}

typealias Quantity = Pair<Int, Int>

