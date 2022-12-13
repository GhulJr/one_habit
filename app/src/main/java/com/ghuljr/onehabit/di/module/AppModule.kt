package com.ghuljr.onehabit.di.module

import android.content.Context
import com.ghuljr.onehabit.App
import com.ghuljr.onehabit_tools.di.ComputationScheduler
import com.ghuljr.onehabit_tools.di.ForApplication
import com.ghuljr.onehabit_tools.di.NetworkScheduler
import com.ghuljr.onehabit_tools.di.UiScheduler
import dagger.Module
import dagger.Provides
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Singleton

@Module
class AppModule(private val app: App) {

    @Singleton
    @get:Provides
    @get:UiScheduler
    val uiScheduler: Scheduler = AndroidSchedulers.mainThread()

    @Singleton
    @get:Provides
    @get:NetworkScheduler
    val networkScheduler: Scheduler = Schedulers.io()

    @Singleton
    @get:Provides
    @get:ComputationScheduler
    val computationScheduler: Scheduler = Schedulers.computation()

    @Singleton
    @Provides
    @ForApplication
    fun context(): Context = app
}