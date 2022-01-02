package com.ghuljr.onehabit.ui.intro.change_data

import com.ghuljr.onehabit.ui.intro.change_data.verify_email.VerifyEmailFinishedFragment
import com.ghuljr.onehabit.ui.intro.change_data.verify_email.VerifyEmailFragment
import com.ghuljr.onehabit.ui.intro.change_data.name.UsernameFragment
import com.ghuljr.onehabit_tools.di.FragmentScope
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface FillUserDataFragmentsModule {

    @FragmentScope
    @ContributesAndroidInjector
    fun verifyEmailFragment(): VerifyEmailFragment

    @FragmentScope
    @ContributesAndroidInjector
    fun verifyEmailFinishedFragment(): VerifyEmailFinishedFragment

    @FragmentScope
    @ContributesAndroidInjector
    fun usernameFragment(): UsernameFragment
}