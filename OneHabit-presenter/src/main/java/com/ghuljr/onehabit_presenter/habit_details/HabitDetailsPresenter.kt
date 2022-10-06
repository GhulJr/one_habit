package com.ghuljr.onehabit_presenter.habit_details

import arrow.core.*
import com.ghuljr.onehabit_data.domain.Milestone
import com.ghuljr.onehabit_data.repository.HabitRepository
import com.ghuljr.onehabit_data.repository.MilestoneRepository
import com.ghuljr.onehabit_data.repository.UserMetadataRepository
import com.ghuljr.onehabit_error.BaseEvent
import com.ghuljr.onehabit_error.LoadingEvent
import com.ghuljr.onehabit_presenter.base.BasePresenter
import com.ghuljr.onehabit_tools.di.ActivityScope
import com.ghuljr.onehabit_tools.di.ComputationScheduler
import com.ghuljr.onehabit_tools.di.UiScheduler
import com.ghuljr.onehabit_tools.extension.mapLeft
import com.ghuljr.onehabit_tools.extension.mapRight
import com.ghuljr.onehabit_tools.extension.onlyRight
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@ActivityScope
class HabitDetailsPresenter @Inject constructor(
    @UiScheduler private val uiScheduler: Scheduler,
    @ComputationScheduler private val computationScheduler: Scheduler,
    private val milestoneRepository: MilestoneRepository,
    private val habitRepository: HabitRepository,
    private val userMetadataRepository: UserMetadataRepository
) : BasePresenter<HabitDetailsView>() {

    private val initSubject = BehaviorSubject.create<String>()
    private val setAsCurrentSubject = PublishSubject.create<Unit>()
    private val openMilestoneDetailsSubject = PublishSubject.create<Pair<String, Int>>()

    override fun subscribeToView(view: HabitDetailsView): Disposable = CompositeDisposable(
        initSubject
            .take(1)
            .switchMap { habitId ->
                Observable.combineLatest(
                    habitRepository.getHabitByIdObservable(habitId),
                    milestoneRepository.getMilestonesOfHabitObservable(habitId),
                ) { habitEither, milestonesEither -> habitEither.zip(milestonesEither) }
                    .observeOn(uiScheduler)
                    .mapLeft { it as BaseEvent }
                    .startWithItem((LoadingEvent as BaseEvent).left())
            }
            .observeOn(uiScheduler)
            .subscribe {
                it.fold(
                    ifRight = { (habit, milestones) ->
                        view.handleEvent(none())
                        view.displayCurrentHabitData(
                            habitTopic = habit.topic,
                            habitSubject = habit.habitSubject,
                            intensityProgress = milestones.firstOrNull { it.resolvedAt == null }?.intensity ?: 0
                        )
                        view.displayMilestoneItems(
                            milestones
                                .filter { it.resolvedAt != null }
                                .sortedBy { it.resolvedAt }
                                .let { list ->
                                    milestones.firstOrNull { it.resolvedAt == null }
                                        ?.let { list.plus(it) } ?: list
                                }
                                .mapIndexed { index, milestone -> milestone.toItem(index + 1) }
                        )
                    },
                    ifLeft = { view.handleEvent(it.some()) }
                )
            },
        initSubject
            .switchMap { habitId -> userMetadataRepository.currentUser.mapRight { it to habitId } }
            .onlyRight()
            .take(1)
            .map { (user, habitId) -> user.habitId != habitId }
            .observeOn(uiScheduler)
            .subscribe { shouldDisplay ->
                view.displaySetAsCurrent(shouldDisplay)
            },
        openMilestoneDetailsSubject
            .throttleFirst(500L, TimeUnit.MILLISECONDS, computationScheduler)
            .observeOn(uiScheduler)
            .subscribe { (milestoneId, orderNumber) ->
                view.openMilestoneDetails(milestoneId, orderNumber)
            },
        setAsCurrentSubject
            .throttleFirst(500L, TimeUnit.MILLISECONDS, computationScheduler)
            .withLatestFrom(initSubject) { _, habitId -> habitId }
            .switchMap {
                userMetadataRepository.setCurrentHabit(it)
                    .toObservable()
                    .mapLeft { it as BaseEvent }
                    .startWithItem(LoadingEvent.left())
            }
            .observeOn(uiScheduler)
            .subscribe {
                it.fold(
                    ifRight = {
                        view.handleEvent(none())
                        view.close()
                    },
                    ifLeft = { view.handleEvent(it.some()) }
                )
            },

        )

    fun init(habitId: String) = initSubject.onNext(habitId)

    fun setAsCurrent() = setAsCurrentSubject.onNext(Unit)

    private fun openMilestoneDetails(milestoneId: String, orderNumber: Int) =
        openMilestoneDetailsSubject.onNext(milestoneId to orderNumber)

    private fun Milestone.toItem(orderNumber: Int) = MilestoneItem(
        id = id,
        orderNumber = orderNumber,
        onClick = { openMilestoneDetails(id, orderNumber) }
    )
}
