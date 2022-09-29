package com.ghuljr.onehabit_tools_android.di

import com.ghuljr.onehabit_data.network.service.*
import com.ghuljr.onehabit_tools_android.network.service.*
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

    @Binds
    @Singleton
    fun goalService(impl: GoalsFirebaseService): GoalsService


    @Binds
    @Singleton
    fun milestoneService(impl: MilestoneFirebaseService): GoalsService
}