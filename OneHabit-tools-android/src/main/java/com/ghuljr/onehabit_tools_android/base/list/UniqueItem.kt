package com.ghuljr.onehabit_tools_android.base.list

interface UniqueItem<T> {
    fun theSame(item: T): Boolean
    fun matches(item: T): Boolean
}