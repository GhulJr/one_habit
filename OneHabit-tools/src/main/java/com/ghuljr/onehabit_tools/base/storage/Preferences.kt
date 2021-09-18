package com.ghuljr.onehabit_tools.base.storage

import arrow.core.Option

interface Preferences {

    fun <T> setValue(key: String, value: T): Boolean
    fun <T> setValue(key: String, valueOption: Option<T>): Boolean
    fun <T> getValue(key: String, defaultValue: T): Option<T>
}