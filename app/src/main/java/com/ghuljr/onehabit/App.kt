package com.ghuljr.onehabit

import android.app.Application
import com.ghuljr.onehabit_tools_android.helper.FirebaseConfigHelper

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        FirebaseConfigHelper.init(this)
    }
}