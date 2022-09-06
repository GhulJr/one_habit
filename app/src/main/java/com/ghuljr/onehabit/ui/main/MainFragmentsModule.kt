package com.ghuljr.onehabit.ui.main

import com.ghuljr.onehabit.ui.main.profile.ProfileFragment
import com.ghuljr.onehabit.ui.main.timeline.TimelineFragment
import com.ghuljr.onehabit.ui.main.today.TodayFragment
import com.ghuljr.onehabit_tools.di.FragmentScope
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface MainFragmentsModule {

    @FragmentScope
    @ContributesAndroidInjector
    fun todayFragment(): TodayFragment

    @FragmentScope
    @ContributesAndroidInjector
    fun timelineFragment(): TimelineFragment

    @FragmentScope
    @ContributesAndroidInjector
    fun profileFragment(): ProfileFragment
}