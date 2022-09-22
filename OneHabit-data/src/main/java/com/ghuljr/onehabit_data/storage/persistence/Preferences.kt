package com.ghuljr.onehabit_data.storage.persistence

import arrow.core.Option

interface Preferences {

    fun <T> setValue(key: String, value: T): Boolean
    fun <T> setValue(key: String, valueOption: Option<T>): Boolean
    fun <T> getValue(key: String, defaultValueOption: Option<T>): Option<T>
    fun clear(): Boolean
}