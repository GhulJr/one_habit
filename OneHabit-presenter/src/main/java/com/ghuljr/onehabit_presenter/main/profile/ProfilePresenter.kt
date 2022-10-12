package com.ghuljr.onehabit_presenter.main.profile

import arrow.core.left
import arrow.core.none
import arrow.core.some
import arrow.core.zip
import com.ghuljr.onehabit_data.repository.HabitRepository
import com.ghuljr.onehabit_data.repository.LoggedInUserRepository
import com.ghuljr.onehabit_data.repository.MilestoneRepository
import com.ghuljr.onehabit_data.repository.UserMetadataRepository
import com.ghuljr.onehabit_error.BaseEvent
import com.ghuljr.onehabit_error.LoadingEvent
import com.ghuljr.onehabit_presenter.base.BasePresenter
import com.ghuljr.onehabit_tools.di.ComputationScheduler
import com.ghuljr.onehabit_tools.di.FragmentScope
import com.ghuljr.onehabit_tools.di.UiScheduler
import com.ghuljr.onehabit_tools.extension.mapLeft
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

// TODO: handle removing habit with all dependencies in withing the app
@FragmentScope
class ProfilePresenter @Inject constructor(
    @UiScheduler private val uiScheduler: Scheduler,
    @ComputationScheduler private val computationScheduler: Scheduler,
    private val userRepository: UserMetadataRepository,
    private val habitRepository: HabitRepository,
    private val milestoneRepository: MilestoneRepository,
    private val loggedInUserRepository: LoggedInUserRepository
) : BasePresenter<ProfileView>() {

    private val openCurrentHabitDetailsSubject = PublishSubject.create<Unit>()
    private val logoutSubject = PublishSubject.create<Unit>()

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
            },
        openCurrentHabitDetailsSubject
            .throttleFirst(500L, TimeUnit.MILLISECONDS, computationScheduler)
            .switchMap {
                userRepository.currentUser
                    .firstElement()
                    .toObservable()
                    .mapLeft { it as BaseEvent }
                    .startWithItem((LoadingEvent as BaseEvent).left())
            }
            .observeOn(uiScheduler)
            .subscribe {
                it.fold(
                    ifRight = { user ->
                        view.handleEvent(none())
                        view.openCurrentHabitDetails(user.habitId ?: "")
                    },
                    ifLeft = { view.handleEvent(it.some()) }
                )
            },
        logoutSubject
            .throttleFirst(500L, TimeUnit.MILLISECONDS, computationScheduler)
            .doOnNext{ loggedInUserRepository.signOut() }
            .observeOn(uiScheduler)
            .subscribe()
    )

    fun openCurrentHabitDetails() = openCurrentHabitDetailsSubject.onNext(Unit)

    fun logout() = logoutSubject.onNext(Unit)
}