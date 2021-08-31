package com.ghuljr.onehabit_tools_android.helper

import android.util.Log
import com.ghuljr.onehabit_tools_android.BuildConfig
import com.google.firebase.database.Logger
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

object FirebaseConfigHelper {

    private var isInitialized = false

    fun init() {
        if(!isInitialized) {
            Firebase.database.setPersistenceEnabled(true)
            Firebase.database.setPersistenceCacheSizeBytes(maxCacheSize)
            Firebase.database.setLogLevel(if(BuildConfig.DEBUG) Logger.Level.DEBUG else Logger.Level.ERROR)

            isInitialized = true
            Log.i(TAG, "Firebase initialized!")
        } else {
            Log.i(TAG, "Firebase already initialized!")
        }
    }

    private const val TAG = "FirebaseConfigHelper"
    private const val maxCacheSize: Long = 20 * 1024 * 1024 // 20 MB
}