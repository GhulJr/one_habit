package com.ghuljr.onehabit.di

import com.ghuljr.onehabit.ApplicationTest
import com.ghuljr.onehabit.di.module.ActivitiesModule
import com.ghuljr.onehabit.di.module.AppModule
import com.ghuljr.onehabit_presenter.validator.ValidatorsModule
import com.ghuljr.onehabit_tools_android.di.ServicesModule
import com.ghuljr.onehabit_tools_android.di.StorageModule
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

@Component(
    modules = [
        AndroidInjectionModule::class,
        AppModule::class,
        ActivitiesModule::class,
        ValidatorsModule::class,
        ServicesModule::class,
        StorageModule::class
    ]
)
@Singleton
interface TestComponent {

    fun inject(applicationTest: ApplicationTest)
}