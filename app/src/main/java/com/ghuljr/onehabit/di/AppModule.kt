package com.ghuljr.onehabit.di

import android.content.Context
import com.ghuljr.onehabit.App
import com.ghuljr.onehabit_tools.di.ComputationScheduler
import com.ghuljr.onehabit_tools.di.ForApplication
import com.ghuljr.onehabit_tools.di.NetworkScheduler
import com.ghuljr.onehabit_tools.di.UiScheduler
import dagger.Module
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Singleton

@Module
class AppModule {

    @Singleton
    @get:UiScheduler
    val uiScheduler: Scheduler = AndroidSchedulers.mainThread()

    @Singleton
    @get:NetworkScheduler
    val networkScheduler: Scheduler = Schedulers.io()

    @Singleton
    @get:ComputationScheduler
    val computationScheduler: Scheduler = Schedulers.computation()

    @ForApplication
    fun context(app: App): Context = app
}