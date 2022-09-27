package com.ghuljr.onehabit.ui.goal_details

import com.ghuljr.onehabit.ui.main.today.ActionsFragment
import com.ghuljr.onehabit_tools.di.FragmentScope
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface DetailsModule {

    @FragmentScope
    @ContributesAndroidInjector
    fun todayFragment(): ActionsFragment
}