package com.ghuljr.onehabit_tools_android.base.storage

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import arrow.core.Option
import arrow.core.none
import arrow.core.toOption
import com.ghuljr.onehabit_tools.base.storage.Preferences

//TODO: setup error handling with Arrow data types
//TODO: pass preferences directly
class PreferencesImpl(context: Context) : Preferences {

    private val preferences: SharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE)

    override fun <T> setValue(key: String, value: T): Boolean = preferences.edit().let { editor ->
        when(value) {
            is Int -> editor.putInt(key, value)
            is Long -> editor.putLong(key, value)
            is Float -> editor.putFloat(key, value)
            is String -> editor.putString(key, value)
            is Boolean -> editor.putBoolean(key, value)
            else -> null
        }.toOption().fold({ false }, { it.commit() })
    }

    override fun <T> setValue(key: String, valueOption: Option<T>): Boolean = valueOption.fold({ false }, { value -> setValue(key, value) })

    @Suppress("UNCHECKED_CAST")
    override fun <T> getValue(key: String, defaultValue: T): Option<T> =
        when {
            !preferences.contains(key) -> none()
            defaultValue is Int -> preferences.getInt(key, defaultValue).toOption() as Option<T>
            defaultValue is Long -> preferences.getLong(key, defaultValue).toOption() as Option<T>
            defaultValue is Float -> preferences.getFloat(key, defaultValue).toOption() as Option<T>
            defaultValue is String -> preferences.getString(key, defaultValue).toOption() as Option<T>
            defaultValue is Boolean -> preferences.getBoolean(key, defaultValue).toOption() as Option<T>
            else -> none()
        }

    companion object {
        private const val SHARED_PREFERENCES = "shared_preferences"
    }
}