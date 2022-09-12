package com.ghuljr.onehabit

import android.util.Log
import androidx.annotation.CallSuper
import com.ghuljr.onehabit.di.DaggerAppComponent
import com.ghuljr.onehabit.di.module.AppModule
import com.ghuljr.onehabit.ui.intro.IntroActivity
import com.ghuljr.onehabit_tools.base.network.ActionResponse
import com.ghuljr.onehabit_tools.extension.onlyFalse
import com.ghuljr.onehabit_tools_android.extension.asSingleTop
import com.ghuljr.onehabit_tools_android.helper.FirebaseConfigHelper
import com.ghuljr.onehabit_tools_android.network.service.LoggedInUserFirebaseService
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import javax.inject.Inject

class App : DaggerApplication() {

    @Inject
    lateinit var loggedInUserFirebaseService: LoggedInUserFirebaseService

    @CallSuper
    override fun onCreate() {
        super.onCreate()

        FirebaseConfigHelper.init(this)
        logRxJavaErrors()

        loggedInUserFirebaseService.isUserLoggedInFlowable
            .onlyFalse()
            .subscribe {
                startActivity(IntroActivity.newIntent(this).asSingleTop())
            }
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> =
        DaggerAppComponent
            .builder()
            .appModule(AppModule(this))
            .build()

    private fun logRxJavaErrors() {
        RxJavaPlugins.setErrorHandler {
            Log.e("GlobalErrorHandler", "error", it)
        }
    }
}