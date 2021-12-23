package com.ghuljr.onehabit.di

import com.ghuljr.onehabit.App
import com.ghuljr.onehabit.di.module.ActivitiesModule
import com.ghuljr.onehabit.di.module.AppModule
import com.ghuljr.onehabit_presenter.validator.ValidatorsModule
import com.ghuljr.onehabit_tools_android.di.ServicesModule
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Component(
    modules = [
        AndroidInjectionModule::class,
        AppModule::class,
        ActivitiesModule::class,
        ServicesModule::class,
        ValidatorsModule::class
    ]
)
@Singleton
interface AppComponent : AndroidInjector<App>