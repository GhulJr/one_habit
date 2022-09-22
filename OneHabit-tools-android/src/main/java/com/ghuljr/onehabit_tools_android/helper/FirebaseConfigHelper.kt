package com.ghuljr.onehabit_tools_android.helper

import android.app.Application
import android.util.Log
import com.ghuljr.onehabit_tools_android.BuildConfig
import com.google.firebase.database.Logger
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize

object FirebaseConfigHelper {

    private var isInitialized = false

    fun init(application: Application) {
        if(!isInitialized) {
            Firebase.initialize(application)
            Firebase.database.setPersistenceEnabled(false)
            Firebase.database.setLogLevel(if(BuildConfig.DEBUG) Logger.Level.DEBUG else Logger.Level.ERROR)
            isInitialized = true
            Log.i(TAG, "Firebase initialized!")
        } else {
            Log.i(TAG, "Firebase already initialized!")
        }
    }

    private const val TAG = "FirebaseConfigHelper"
}
