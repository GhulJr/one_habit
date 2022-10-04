package com.ghuljr.onehabit_presenter.habit_details

import com.ghuljr.onehabit_data.repository.MilestoneRepository
import com.ghuljr.onehabit_presenter.base.BasePresenter
import com.ghuljr.onehabit_tools.di.ActivityScope
import com.ghuljr.onehabit_tools.di.UiScheduler
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import javax.inject.Inject

@ActivityScope
class HabitDetailsPresenter @Inject constructor(
    @UiScheduler private val uiScheduler: Scheduler,
    private val milestoneRepository: MilestoneRepository
) : BasePresenter<HabitDetailsView>() {

    private val initSubject = BehaviorSubject.create<String>()

    override fun subscribeToView(view: HabitDetailsView): Disposable = CompositeDisposable(
        initSubject
            .take(1)
           // .switchMap {  }
            .observeOn(uiScheduler)
            .subscribe {

            }
    )

    fun init(habitId: String) = initSubject.onNext(habitId)
}