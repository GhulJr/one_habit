package com.ghuljr.onehabit_tools.base.list

interface UniqueItem {
    fun theSame(item: UniqueItem): Boolean
    fun  matches(item: UniqueItem): Boolean
}