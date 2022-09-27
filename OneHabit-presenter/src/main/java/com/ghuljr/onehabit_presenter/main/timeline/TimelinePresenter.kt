package com.ghuljr.onehabit_presenter.main.timeline

import arrow.core.left
import arrow.core.none
import arrow.core.some
import com.ghuljr.onehabit_data.domain.Goal
import com.ghuljr.onehabit_data.repository.GoalRepository
import com.ghuljr.onehabit_error.BaseEvent
import com.ghuljr.onehabit_error.LoadingEvent
import com.ghuljr.onehabit_presenter.base.BasePresenter
import com.ghuljr.onehabit_tools.di.ComputationScheduler
import com.ghuljr.onehabit_tools.di.FragmentScope
import com.ghuljr.onehabit_tools.di.UiScheduler
import com.ghuljr.onehabit_tools.extension.mapLeft
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@FragmentScope
class TimelinePresenter @Inject constructor(
    @UiScheduler private val uiScheduler: Scheduler,
    @ComputationScheduler private val computationScheduler: Scheduler,
    private val goalRepository: GoalRepository
) : BasePresenter<TimelineView>() {

    private val refreshSubject = PublishSubject.create<Unit>()
    private val openGoalDetailsSubject = PublishSubject.create<String>()

    override fun subscribeToView(view: TimelineView): Disposable = CompositeDisposable(
        goalRepository.currentGoals
            .mapLeft { it as BaseEvent }
            .startWithItem(LoadingEvent.left())
            .observeOn(uiScheduler)
            .subscribe {
                it.fold(
                    ifRight = { goals ->
                        view.submitItems(
                            listOf(HeaderItem)
                                .plus(goals.map { it.toItem() })
                                .plus(
                                    SummaryItem(
                                        dayNumber = (Calendar.getInstance()
                                            .get(Calendar.DAY_OF_WEEK) - 1) % 7,
                                        totalDays = goals.size
                                    )
                                )
                        )
                        view.handleEvent(none())
                    },
                    ifLeft = { view.handleEvent(it.some()) }
                )
            },
        refreshSubject
            .throttleFirst(500L, TimeUnit.MILLISECONDS, computationScheduler)
            .switchMap {
                goalRepository.refreshCurrentGoal()
                    .toObservable()
                    .mapLeft { it as BaseEvent }
                    .startWithItem(LoadingEvent.left())
            }
            .observeOn(uiScheduler)
            .subscribe {
                it.fold(
                    ifRight = { view.handleEvent(none()) },
                    ifLeft = { view.handleEvent(it.some()) }
                )
            },
        openGoalDetailsSubject
            .throttleFirst(500L, TimeUnit.MILLISECONDS, computationScheduler)
            .observeOn(uiScheduler)
            .subscribe { view.openGoalDetails(it) }
    )


    fun refresh() = refreshSubject.onNext(Unit)

    private fun Goal.toItem() = GoalItem(
        id = id,
        dayNumber = dayNumber.toInt() + 1,
        state = getState(),
        onClick = { openGoalDetails(id) }
    )

    private fun Goal.getState(): GoalItem.State {
        val currentDayNumber = (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1) % 7 - 1
        return when {
            dayNumber.toInt() == currentDayNumber -> GoalItem.State.Today
            dayNumber < currentDayNumber -> {
                GoalItem.State.Past.Success
            }
            else -> GoalItem.State.Future
        }
    }

    private fun openGoalDetails(goalId: String) = openGoalDetailsSubject.onNext(goalId)
}