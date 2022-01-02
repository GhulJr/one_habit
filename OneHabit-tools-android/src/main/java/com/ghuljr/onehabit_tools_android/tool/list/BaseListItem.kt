package com.ghuljr.onehabit_tools_android.tool.list

abstract class BaseListItem<ID> {

    abstract fun itemId(): ID
    open fun areItemsTheSame(item: BaseListItem<*>): Boolean = itemId() == item.itemId()
    open fun areContentsTheSame(item: BaseListItem<*>): Boolean = this == item
}