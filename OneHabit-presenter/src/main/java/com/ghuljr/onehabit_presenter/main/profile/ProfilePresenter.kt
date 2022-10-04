package com.ghuljr.onehabit_presenter.main.profile

import arrow.core.left
import arrow.core.none
import arrow.core.some
import arrow.core.zip
import com.ghuljr.onehabit_data.repository.HabitRepository
import com.ghuljr.onehabit_data.repository.LoggedInUserRepository
import com.ghuljr.onehabit_data.repository.MilestoneRepository
import com.ghuljr.onehabit_error.BaseEvent
import com.ghuljr.onehabit_error.LoadingEvent
import com.ghuljr.onehabit_presenter.base.BasePresenter
import com.ghuljr.onehabit_tools.di.FragmentScope
import com.ghuljr.onehabit_tools.di.UiScheduler
import com.ghuljr.onehabit_tools.extension.mapLeft
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import javax.inject.Inject
import kotlin.math.abs

// TODO: handle removing habit with all dependencies in withing the app
@FragmentScope
class ProfilePresenter @Inject constructor(
    @UiScheduler private val uiScheduler: Scheduler,
    private val userRepository: LoggedInUserRepository,
    private val habitRepository: HabitRepository,
    private val milestoneRepository: MilestoneRepository
) : BasePresenter<ProfileView>() {

    override fun subscribeToView(view: ProfileView): Disposable = CompositeDisposable(
        Observable.combineLatest(
            habitRepository.todayHabitObservable,
            milestoneRepository.todayMilestoneObservable
        ) { habitEither, milestoneEither -> habitEither.zip(milestoneEither) }
            .observeOn(uiScheduler)
            .mapLeft { it as BaseEvent }
            .startWithItem((LoadingEvent as BaseEvent).left())
            .subscribe {
                it.fold(
                    ifRight = { (habit, milestone) ->
                        view.handleEvent(none())
                        view.displayCurrentHabitData(
                            habitTopic = habit.topic,
                            habitSubject = habit.habitSubject,
                            intensityProgress = milestone.intensity
                        )
                    },
                    ifLeft = { view.handleEvent(it.some()) }
                )
            }
    )
}