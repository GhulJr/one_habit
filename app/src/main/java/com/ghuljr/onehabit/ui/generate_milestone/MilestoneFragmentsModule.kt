package com.ghuljr.onehabit.ui.generate_milestone

import com.ghuljr.onehabit.ui.generate_milestone.generate.AdjustIntensityFragment
import com.ghuljr.onehabit.ui.generate_milestone.generated.AcceptGeneratedMilestoneFragment
import com.ghuljr.onehabit.ui.generate_milestone.intro.MilestoneIntroFragment
import com.ghuljr.onehabit.ui.generate_milestone.summary.SummarisePreviousMilestoneFragment
import com.ghuljr.onehabit_tools.di.FragmentScope
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface MilestoneFragmentsModule {

    @FragmentScope
    @ContributesAndroidInjector
    fun introFragment() : MilestoneIntroFragment

    @FragmentScope
    @ContributesAndroidInjector
    fun summaryFragment() : SummarisePreviousMilestoneFragment

    @FragmentScope
    @ContributesAndroidInjector
    fun adjustIntensityFragment() : AdjustIntensityFragment

    @FragmentScope
    @ContributesAndroidInjector
    fun acceptMilestoneFragment() : AcceptGeneratedMilestoneFragment



}