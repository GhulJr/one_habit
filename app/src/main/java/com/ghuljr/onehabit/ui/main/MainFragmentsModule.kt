package com.ghuljr.onehabit.ui.main

import com.ghuljr.onehabit.ui.main.today.TodayFragment
import com.ghuljr.onehabit_tools.di.FragmentScope
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface MainFragmentsModule {

    @FragmentScope
    @ContributesAndroidInjector
    fun todayFragment(): TodayFragment
}