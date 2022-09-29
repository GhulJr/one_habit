package com.ghuljr.onehabit_presenter.generate_milestone.generate

import arrow.core.*
import com.ghuljr.onehabit_data.repository.HabitRepository
import com.ghuljr.onehabit_data.repository.MilestoneRepository
import com.ghuljr.onehabit_data.repository.UserMetadataRepository
import com.ghuljr.onehabit_error.BaseEvent
import com.ghuljr.onehabit_error.LoadingEvent
import com.ghuljr.onehabit_presenter.base.BasePresenter
import com.ghuljr.onehabit_tools.di.ComputationScheduler
import com.ghuljr.onehabit_tools.di.FragmentScope
import com.ghuljr.onehabit_tools.di.UiScheduler
import com.ghuljr.onehabit_tools.extension.mapLeft
import com.ghuljr.onehabit_tools.extension.mapRight
import com.ghuljr.onehabit_tools.extension.switchMapRightWithEither
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject

@FragmentScope
class AdjustIntensityPresenter @Inject constructor(
    @UiScheduler private val uiScheduler: Scheduler,
    @ComputationScheduler private val computationScheduler: Scheduler,
    private val milestoneRepository: MilestoneRepository,
    private val habitRepository: HabitRepository,
    private val userMetadataRepository: UserMetadataRepository
) : BasePresenter<AdjustIntensityView>() {

    private val intensitySubject = BehaviorSubject.create<Float>()

    override fun subscribeToView(view: AdjustIntensityView): Disposable = CompositeDisposable(
        userMetadataRepository.currentUser
            .mapLeft { it as BaseEvent }
            .switchMapRightWithEither {
                if (it.milestoneId == null)
                    Observable.just((0.0 to 25.0).right())
                else
                    Observable.combineLatest(
                        habitRepository.todayHabitObservable,
                        milestoneRepository.getMilestoneByIdObservable(it.milestoneId!!)
                    ) { habit, milestone -> habit.zip(milestone) }
                        .mapRight { (habit, milestone) -> milestone.intensity.toDouble() to milestone.intensity.toFloat() + habit.defaultProgressFactor.toDouble() }
                        .mapLeft { it as BaseEvent }
                        .startWithItem(LoadingEvent.left())
            }
            .observeOn(uiScheduler)
            .takeUntil { it.isRight() }
            .subscribe {
                it.fold(
                    ifRight = {
                        view.handleEvent(none())
                        view.setSliderScope(it)
                    },
                    ifLeft = { view.handleEvent(it.some()) }
                )
            }
    )

    fun setIntensity(intensity: Float) = intensitySubject.onNext(intensity)
}