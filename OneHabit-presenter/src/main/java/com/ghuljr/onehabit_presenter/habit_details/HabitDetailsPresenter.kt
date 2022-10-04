package com.ghuljr.onehabit_presenter.habit_details

import arrow.core.left
import arrow.core.none
import arrow.core.some
import arrow.core.zip
import com.ghuljr.onehabit_data.repository.HabitRepository
import com.ghuljr.onehabit_data.repository.MilestoneRepository
import com.ghuljr.onehabit_data.repository.UserMetadataRepository
import com.ghuljr.onehabit_error.BaseEvent
import com.ghuljr.onehabit_error.LoadingEvent
import com.ghuljr.onehabit_presenter.base.BasePresenter
import com.ghuljr.onehabit_tools.di.ActivityScope
import com.ghuljr.onehabit_tools.di.UiScheduler
import com.ghuljr.onehabit_tools.extension.mapLeft
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import javax.inject.Inject

@ActivityScope
class HabitDetailsPresenter @Inject constructor(
    @UiScheduler private val uiScheduler: Scheduler,
    private val milestoneRepository: MilestoneRepository,
    private val habitRepository: HabitRepository,
) : BasePresenter<HabitDetailsView>() {

    private val initSubject = BehaviorSubject.create<String>()

    override fun subscribeToView(view: HabitDetailsView): Disposable = CompositeDisposable(
        initSubject
            .take(1)
            .switchMap { habitId ->
                Observable.combineLatest(
                    habitRepository.getHabitByIdObservable(habitId),
                    milestoneRepository.getMilestonesOfHabitObservable(habitId),
                    milestoneRepository.todayMilestoneObservable
                ) { habitEither, milestonesEither, currentMilestoneEither ->
                    habitEither
                        .zip(milestonesEither, currentMilestoneEither) { habit, milestones, currentMilestone ->
                            Triple(habit, milestones, currentMilestone)
                        }
                }
                    .observeOn(uiScheduler)
                    .mapLeft { it as BaseEvent }
                    .startWithItem((LoadingEvent as BaseEvent).left())
            }
            .observeOn(uiScheduler)
            .subscribe {
                it.fold(
                    ifRight = { (habit, milestones, currentMilestone) ->
                        view.handleEvent(none())
                        view.displayCurrentHabitData(
                            habitTopic = habit.topic,
                            habitSubject = habit.habitSubject,
                            intensityProgress = currentMilestone.intensity
                        )
                    },
                    ifLeft = { view.handleEvent(it.some()) }
                )
            }
    )

    fun init(habitId: String) = initSubject.onNext(habitId)
}