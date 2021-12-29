package com.ghuljr.onehabit_tools_android.di

import com.ghuljr.onehabit_tools.base.storage.Preferences
import com.ghuljr.onehabit_tools_android.base.storage.PreferencesImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton


@Module
interface StorageModule {

    @Binds
    @Singleton
    fun preferences(impl: PreferencesImpl): Preferences
}