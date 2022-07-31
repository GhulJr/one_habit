package com.ghuljr.onehabit_tools_android.base.list

interface UniqueItem {
    fun theSame(item: UniqueItem): Boolean
    fun  matches(item: UniqueItem): Boolean
}