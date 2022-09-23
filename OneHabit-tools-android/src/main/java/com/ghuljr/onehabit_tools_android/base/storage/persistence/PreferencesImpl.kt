package com.ghuljr.onehabit_tools_android.base.storage.persistence

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import arrow.core.Option
import arrow.core.none
import arrow.core.toOption
import com.ghuljr.onehabit_data.storage.persistence.Preferences
import com.ghuljr.onehabit_tools.di.ForApplication
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesImpl @Inject constructor(@ForApplication context: Context) : Preferences {

    private val preferences: SharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE)

    override fun <T> setValue(key: String, valueOption: Option<T>): Boolean = valueOption.fold({ valueOption.isEmpty() }, { value -> setValue(key, value) })

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

    @Suppress("UNCHECKED_CAST")
    override fun <T> getValue(key: String, defaultValueOption: Option<T>): Option<T> = defaultValueOption.flatMap { defaultValue ->
        getValue(key, defaultValue)
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> getValue(key: String, defaultValue: T): Option<T> =
        when {
            !preferences.contains(key) -> none()
            defaultValue is Int -> preferences.getInt(key, defaultValue).toOption() as Option<T>
            defaultValue is Long -> preferences.getLong(key, defaultValue).toOption() as Option<T>
            defaultValue is Float -> preferences.getFloat(key, defaultValue).toOption() as Option<T>
            defaultValue is String -> preferences.getString(key, defaultValue).toOption() as Option<T>
            defaultValue is Boolean -> preferences.getBoolean(key, defaultValue).toOption() as Option<T>
            else -> none()
        }


    override fun clear(): Boolean = preferences.edit().clear().commit()

    companion object {
        private const val SHARED_PREFERENCES = "shared_preferences"
    }
}