package com.ghuljr.onehabit_tools_android.di

import com.ghuljr.onehabit_data.network.service.ActionsService
import com.ghuljr.onehabit_data.network.service.LoggedInUserService
import com.ghuljr.onehabit_tools_android.network.service.ActionsFirebaseService
import com.ghuljr.onehabit_tools_android.network.service.LoggedInUserFirebaseService
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
    fun actionsService(impl: ActionsService): ActionsFirebaseService
}