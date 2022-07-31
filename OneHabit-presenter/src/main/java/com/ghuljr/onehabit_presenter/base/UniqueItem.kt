package com.ghuljr.onehabit_presenter.base

interface UniqueItem {
    fun theSame(item: UniqueItem): Boolean
    fun  matches(item: UniqueItem): Boolean
}