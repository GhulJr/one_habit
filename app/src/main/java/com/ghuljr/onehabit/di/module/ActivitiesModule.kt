package com.ghuljr.onehabit.di.module

import com.ghuljr.onehabit.ui.intro.login.LoginActivity
import com.ghuljr.onehabit.ui.intro.register.RegisterActivity
import com.ghuljr.onehabit.ui.intro.register.RegisterFragmentsModule
import com.ghuljr.onehabit.ui.main.MainActivity
import com.ghuljr.onehabit.ui.main.MainModule
import com.ghuljr.onehabit_tools.di.ActivityScope
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface ActivitiesModule {

    @ActivityScope
    @ContributesAndroidInjector(modules = [MainModule::class])
    fun mainActivity(): MainActivity

    @ActivityScope
    @ContributesAndroidInjector
    fun loginActivity(): LoginActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [RegisterFragmentsModule::class])
    fun registerActivity(): RegisterActivity
}