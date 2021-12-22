package com.ghuljr.onehabit.ui.intro.register

import com.ghuljr.onehabit.ui.intro.register.credentials.RegisterCredentialsFragment
import com.ghuljr.onehabit_tools.di.FragmentScope
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface RegisterFragmentsModule {

    @FragmentScope
    @ContributesAndroidInjector
    fun registerCredentialsFragment(): RegisterCredentialsFragment
}