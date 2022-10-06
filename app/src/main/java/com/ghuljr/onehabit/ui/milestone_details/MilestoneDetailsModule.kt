package com.ghuljr.onehabit.ui.milestone_details

import com.ghuljr.onehabit.ui.main.timeline.TimelineFragment
import com.ghuljr.onehabit_tools.di.FragmentScope
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
interface MilestoneDetailsModule {

    @FragmentScope
    @ContributesAndroidInjector
    fun timelineFragment() : TimelineFragment
}