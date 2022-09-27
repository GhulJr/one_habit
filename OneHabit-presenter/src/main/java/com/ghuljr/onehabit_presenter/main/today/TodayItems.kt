package com.ghuljr.onehabit_presenter.main.today

import com.ghuljr.onehabit_tools.base.list.UniqueItem
import com.ghuljr.onehabit_tools.model.HabitTopic

sealed interface TodayItem : UniqueItem {

    sealed class Action : TodayItem {
        abstract val id: String
        abstract val time: String?
        abstract val quantity: Quantity?
        protected abstract val onActionClick: () -> Unit
    }
}

data class TodayActionItem(
    override val id: String,
    override val time: String?,
    override val quantity: Quantity?,
    override val onActionClick: () -> Unit,
    val habitTopic: HabitTopic,
    val habitSubject: String,
    val actionType: ActionType,
    val exceeded: Boolean
) : TodayItem.Action() {

    override fun theSame(item: UniqueItem): Boolean = (item as? TodayActionItem)?.id == id
    override fun matches(item: UniqueItem): Boolean = item == this

    fun openActionDetails() = onActionClick()
}

data class CustomActionItem(
    override val id: String,
    override val time: String?,
    override val onActionClick: () -> Unit,
    val title: String,
    val habitTopic: HabitTopic,
    val habitSubject: String
) : TodayItem.Action() {

    override val quantity: Quantity? = null

    override fun theSame(item: UniqueItem): Boolean = (item as? CustomActionItem)?.id == id
    override fun matches(item: UniqueItem): Boolean = item == this

    fun openActionDetails() = onActionClick()
}

data class TodayActionFinishedItem(
    override val id: String,
    override val time: String?,
    override val quantity: Quantity?,
    override val onActionClick: () -> Unit,
    val customTitle: String?,
    val habitTopic: HabitTopic,
    val habitSubject: String
) : TodayItem.Action() {

    override fun theSame(item: UniqueItem): Boolean = (item as? TodayActionFinishedItem)?.id == id
    override fun matches(item: UniqueItem): Boolean = item == this

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

data class ActionInfoItem(
    val customTitle: String?,
    val editable: Boolean,
    val habitTopic: HabitTopic,
    val quantity: Quantity?,
    val habitSubject: String,
    val type: ActionType,
    val reminders: List<String>?,
    val exceeded: Boolean,
    val declineAvailable: Boolean,
    val confirmAvailable: Boolean
)

enum class ActionType {
    DAILY, WEEKLY
}

typealias Quantity = Pair<Int, Int>

