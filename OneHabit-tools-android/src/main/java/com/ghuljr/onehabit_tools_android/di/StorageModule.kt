package com.ghuljr.onehabit_tools_android.di

import android.content.Context
import com.ghuljr.onehabit_data.storage.model.*
import com.ghuljr.onehabit_data.storage.persistence.Preferences
import com.ghuljr.onehabit_tools.di.ForApplication
import com.ghuljr.onehabit_tools_android.base.storage.persistence.PreferencesImpl
import dagger.Module
import dagger.Provides
import io.objectbox.Box
import io.objectbox.BoxStore
import javax.inject.Singleton


@Module
class StorageModule {

    @Provides
    @Singleton
    fun preferences(impl: PreferencesImpl): Preferences = impl

    @Provides
    @Singleton
    fun boxStore(@ForApplication context: Context): BoxStore = MyObjectBox.builder()
        .androidContext(context.applicationContext)
        .build()

    @Provides
    @Singleton
    fun actionBox(boxStore: BoxStore): Box<ActionEntity> = boxStore.boxFor(ActionEntity::class.java)

    @Provides
    @Singleton
    fun actionHolderBox(boxStore: BoxStore): Box<ActionOfGoalEntitiesHolder> = boxStore.boxFor(ActionOfGoalEntitiesHolder::class.java)

    @Provides
    @Singleton
    fun userBox(boxStore: BoxStore): Box<UserEntity> = boxStore.boxFor(UserEntity::class.java)

    @Provides
    @Singleton
    fun userHolderBox(boxStore: BoxStore): Box<UserEntityHolder> = boxStore.boxFor(UserEntityHolder::class.java)

    @Provides
    @Singleton
    fun habitBox(boxStore: BoxStore): Box<HabitEntity> = boxStore.boxFor(HabitEntity::class.java)

    @Provides
    @Singleton
    fun habitHolderBox(boxStore: BoxStore): Box<HabitEntityHolder> = boxStore.boxFor(HabitEntityHolder::class.java)
}

