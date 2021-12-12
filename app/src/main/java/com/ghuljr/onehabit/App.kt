package com.ghuljr.onehabit

import androidx.annotation.CallSuper
import com.ghuljr.onehabit.di.DaggerAppComponent
import com.ghuljr.onehabit_tools_android.helper.FirebaseConfigHelper
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication

class App : DaggerApplication() {

    @CallSuper
    override fun onCreate() {
        super.onCreate()
        FirebaseConfigHelper.init(this)
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> = DaggerAppComponent.create()
}