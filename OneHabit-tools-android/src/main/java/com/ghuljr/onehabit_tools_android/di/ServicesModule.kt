package com.ghuljr.onehabit_tools_android.di

import com.ghuljr.onehabit_tools.base.network.LoggedInUserService
import com.ghuljr.onehabit_tools_android.network.service.LoggedInUserFirebaseService
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
interface ServicesModule {

    @Binds
    @Singleton
    fun loggedInUserService(impl: LoggedInUserFirebaseService): LoggedInUserService
}