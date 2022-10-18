package com.ghuljr.onehabit_presenter.main.timeline

import arrow.core.*
import com.ghuljr.onehabit_data.domain.Goal
import com.ghuljr.onehabit_data.repository.GoalRepository
import com.ghuljr.onehabit_data.repository.UserMetadataRepository
import com.ghuljr.onehabit_error.BaseEvent
import com.ghuljr.onehabit_error.LoadingEvent
import com.ghuljr.onehabit_presenter.base.BasePresenter
import com.ghuljr.onehabit_tools.di.ComputationScheduler
import com.ghuljr.onehabit_tools.di.FragmentScope
import com.ghuljr.onehabit_tools.di.UiScheduler
import com.ghuljr.onehabit_tools.extension.mapLeft
import com.ghuljr.onehabit_tools.extension.mapRight
import com.ghuljr.onehabit_tools.extension.startWithLoading
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@FragmentScope
class TimelinePresenter @Inject constructor(
    @UiScheduler private val uiScheduler: Scheduler,
    @ComputationScheduler private val computationScheduler: Scheduler,
    private val goalRepository: GoalRepository,
    private val userMetadataRepository: UserMetadataRepository
) : BasePresenter<TimelineView>() {

    private val refreshSubject = PublishSubject.create<Unit>()
    private val openGoalDetailsSubject = PublishSubject.create<Pair<String, Int>>()
    private val initSubject = BehaviorSubject.create<Option<String>>()

    override fun subscribeToView(view: TimelineView): Disposable = CompositeDisposable(
        initSubject
            .take(1)
            .switchMap { idOption ->
                Observable.combineLatest(
                    idOption.fold(
                        ifSome = { milestoneId -> goalRepository.getGoalsByMilestoneId(milestoneId) },
                        ifEmpty = { goalRepository.currentGoals }
                    ),
                    userMetadataRepository.currentUser
                ) { goals, user -> goals.zip(user) }
                    .mapLeft { it as BaseEvent }
                    .startWithLoading()
                    .mapRight { (goals, user) ->
                        listOf(HeaderItem)
                            .plus(goals.map { goal ->
                                goal.toItem(
                                    idOption.fold(
                                        ifSome = { milestoneId ->
                                            if (milestoneId == user.milestoneId)
                                                goal.getState()
                                            else GoalItem.State.Past.Success
                                        },
                                        ifEmpty = { goal.getState() }
                                    )
                                )
                            })
                            .plus(

                                SummaryItem(
                                    dayNumber = idOption.fold(
                                        ifSome = { 7 },
                                        ifEmpty = {
                                            (Calendar.getInstance()
                                                .get(Calendar.DAY_OF_WEEK) - 1) % 7
                                        }
                                    ),
                                    totalDays = goals.size
                                )
                            )
                    }

            }
            .observeOn(uiScheduler)
            .subscribe {
                it.fold(
                    ifRight = { goals ->
                        view.submitItems(goals)
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
                    .startWithLoading()
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
            .subscribe { (id, orderNumber) -> view.openGoalDetails(id, orderNumber) }
    )


    fun refresh() = refreshSubject.onNext(Unit)
    fun init(milestoneId: Option<String>) = initSubject.onNext(milestoneId)

    private fun Goal.toItem(state: GoalItem.State) = GoalItem(
        id = id,
        dayNumber = dayNumber.toInt() + 1,
        state = state,
        onClick = { openGoalDetails(id, dayNumber.toInt() + 1) }
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

    private fun openGoalDetails(goalId: String, dayNumber: Int) =
        openGoalDetailsSubject.onNext(goalId to dayNumber)
}