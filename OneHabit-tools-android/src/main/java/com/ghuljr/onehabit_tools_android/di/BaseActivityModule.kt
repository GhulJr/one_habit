package com.ghuljr.onehabit_tools_android.di

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.ghuljr.onehabit_tools.di.ActivityScope
import com.ghuljr.onehabit_tools.di.ForActivity
import dagger.Module
import dagger.Provides

@Module
abstract class BaseActivityModule<A: AppCompatActivity> {

    @Provides
    @ForActivity
    fun activity(activity: A): A = activity

    @Provides
    @ForActivity
    @ActivityScope
    fun context(@ForActivity activity: A): Context = activity

}