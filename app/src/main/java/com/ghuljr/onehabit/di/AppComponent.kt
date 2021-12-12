package com.ghuljr.onehabit.di

import com.ghuljr.onehabit.App
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector

@Component(
    modules = [
        AndroidInjectionModule::class,
        AppModule::class,
        ActivitiesModule::class,
        FragmentsModule::class
    ]
)
interface AppComponent : AndroidInjector<App>