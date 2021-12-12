package com.ghuljr.onehabit

import android.util.Log
import androidx.annotation.CallSuper
import com.ghuljr.onehabit.di.DaggerAppComponent
import com.ghuljr.onehabit_tools_android.helper.FirebaseConfigHelper
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import io.reactivex.rxjava3.plugins.RxJavaPlugins

class App : DaggerApplication() {

    @CallSuper
    override fun onCreate() {
        super.onCreate()
        FirebaseConfigHelper.init(this)
        logRxJavaErrors()
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> = DaggerAppComponent.create()

    private fun logRxJavaErrors() {
        RxJavaPlugins.setErrorHandler {
            Log.e("GlobalErrorHandler", "error", it)
        }
    }
}