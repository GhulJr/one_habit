package com.ghuljr.onehabit

import android.app.Application
import androidx.annotation.CallSuper
import com.ghuljr.onehabit_presenter.dependency_injection.dataModule
import com.ghuljr.onehabit_presenter.dependency_injection.presenterModule
import com.ghuljr.onehabit_tools_android.firebaseModule
import com.ghuljr.onehabit_tools_android.helper.FirebaseConfigHelper
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : Application() {

    @CallSuper
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(presenterModule, dataModule, firebaseModule)
        }

        FirebaseConfigHelper.init(this)
    }
}