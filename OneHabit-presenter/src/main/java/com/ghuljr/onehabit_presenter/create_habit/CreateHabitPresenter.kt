package com.ghuljr.onehabit_presenter.create_habit

import com.ghuljr.onehabit_data.repository.HabitRepository
import com.ghuljr.onehabit_presenter.base.BasePresenter
import com.ghuljr.onehabit_tools.di.ActivityScope
import com.ghuljr.onehabit_tools.di.UiScheduler
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import javax.inject.Inject

// TODO: handle removing habit with all dependencies in withing the app
@ActivityScope
class CreateHabitPresenter @Inject constructor(
    @UiScheduler private val uiScheduler: Scheduler,
    private val habitRepository: HabitRepository
) : BasePresenter<CreateHabitView>() {



    override fun subscribeToView(view: CreateHabitView): Disposable = CompositeDisposable(

    )
}