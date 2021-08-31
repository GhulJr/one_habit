package com.ghuljr.onehabit

import android.app.Application
import com.ghuljr.onehabit_tools_android.helper.FirebaseConfigHelper
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        FirebaseConfigHelper.init()
    }
}