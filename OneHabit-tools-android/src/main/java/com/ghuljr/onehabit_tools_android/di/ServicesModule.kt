package com.ghuljr.onehabit_tools_android.di

import com.ghuljr.onehabit_data.network.service.ActionsService
import com.ghuljr.onehabit_data.network.service.HabitService
import com.ghuljr.onehabit_data.network.service.LoggedInUserService
import com.ghuljr.onehabit_data.network.service.UserService
import com.ghuljr.onehabit_tools_android.network.service.ActionsFirebaseService
import com.ghuljr.onehabit_tools_android.network.service.HabitFirebaseService
import com.ghuljr.onehabit_tools_android.network.service.LoggedInUserFirebaseService
import com.ghuljr.onehabit_tools_android.network.service.UserFirebaseService
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
interface ServicesModule {

    @Binds
    @Singleton
    fun loggedInUserService(impl: LoggedInUserFirebaseService): LoggedInUserService

    @Binds
    @Singleton
    fun actionsService(impl: ActionsFirebaseService): ActionsService

    @Binds
    @Singleton
    fun userService(impl: UserFirebaseService): UserService

    @Binds
    @Singleton
    fun habitService(impl: HabitFirebaseService): HabitService
}