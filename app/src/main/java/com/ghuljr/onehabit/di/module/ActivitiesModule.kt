package com.ghuljr.onehabit.di.module

import com.ghuljr.onehabit.ui.intro.IntroActivity
import com.ghuljr.onehabit.ui.intro.change_data.FillUserDataActivity
import com.ghuljr.onehabit.ui.intro.change_data.FillUserDataFragmentsModule
import com.ghuljr.onehabit.ui.intro.launch.LaunchActivity
import com.ghuljr.onehabit.ui.intro.login.LoginActivity
import com.ghuljr.onehabit.ui.intro.login.forgot_password.ForgotPasswordActivity
import com.ghuljr.onehabit.ui.intro.register.RegisterActivity
import com.ghuljr.onehabit.ui.intro.register.RegisterFragmentsModule
import com.ghuljr.onehabit.ui.main.MainActivity
import com.ghuljr.onehabit.ui.main.MainFragmentsModule
import com.ghuljr.onehabit_tools.di.ActivityScope
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface ActivitiesModule {

    @ActivityScope
    @ContributesAndroidInjector(modules = [MainFragmentsModule::class])
    fun mainActivity(): MainActivity

    @ActivityScope
    @ContributesAndroidInjector
    fun loginActivity(): LoginActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [RegisterFragmentsModule::class])
    fun registerActivity(): RegisterActivity

    @ActivityScope
    @ContributesAndroidInjector
    fun launchActivity(): LaunchActivity

    @ActivityScope
    @ContributesAndroidInjector
    fun introActivity(): IntroActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [FillUserDataFragmentsModule::class])
    fun fillUserDataActivity(): FillUserDataActivity

    @ActivityScope
    @ContributesAndroidInjector
    fun forgotPasswordActivity(): ForgotPasswordActivity
}