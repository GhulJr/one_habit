package com.ghuljr.onehabit_presenter.goal_details

import com.ghuljr.onehabit_data.repository.GoalRepository
import com.ghuljr.onehabit_presenter.base.BasePresenter
import com.ghuljr.onehabit_tools.di.ActivityScope
import com.ghuljr.onehabit_tools.di.UiScheduler
import com.ghuljr.onehabit_tools.extension.mapRight
import com.ghuljr.onehabit_tools.extension.onlyRight
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import javax.inject.Inject

@ActivityScope
class GoalDetailsPresenter @Inject constructor(
    @UiScheduler private val uiScheduler: Scheduler,
    private val goalRepository: GoalRepository
) : BasePresenter<GoalDetailsView>() {

    private val goalIdSubject = BehaviorSubject.create<String>()

    override fun subscribeToView(view: GoalDetailsView): Disposable = CompositeDisposable(
        goalIdSubject
            .take(1)
            .switchMap { goalId ->
                goalRepository.currentGoals
                    .mapRight { it.map { it.id }.indexOf(goalId) + 1 }
            }
            .onlyRight()
            .observeOn(uiScheduler)
            .subscribe { dayNumber ->
                view.displayDayNumber(dayNumber)
            }
    )

    fun init(goalId: String) = goalIdSubject.onNext(goalId)
}