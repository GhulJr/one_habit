package com.ghuljr.onehabit_presenter.create_habit

import arrow.core.left
import arrow.core.none
import arrow.core.some
import com.ghuljr.onehabit_data.repository.HabitRepository
import com.ghuljr.onehabit_error.BaseEvent
import com.ghuljr.onehabit_error.LoadingEvent
import com.ghuljr.onehabit_presenter.base.BasePresenter
import com.ghuljr.onehabit_tools.di.ActivityScope
import com.ghuljr.onehabit_tools.di.ComputationScheduler
import com.ghuljr.onehabit_tools.di.UiScheduler
import com.ghuljr.onehabit_tools.extension.mapLeft
import com.ghuljr.onehabit_tools.model.HabitTopic
import com.ghuljr.onehabit_tools.model.WeeklyFlags
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@ActivityScope
class CreateHabitPresenter @Inject constructor(
    @ComputationScheduler private val computationScheduler: Scheduler,
    @UiScheduler private val uiScheduler: Scheduler,
    private val habitRepository: HabitRepository
) : BasePresenter<CreateHabitView>() {

    private val currentStepSubject = BehaviorSubject.createDefault(Step.ACTIVITY)
    private val habitActionSubject = BehaviorSubject.create<HabitTopic>()
    private val habitTopicSubject = BehaviorSubject.createDefault("")
    private val habitBaseIntensity = BehaviorSubject.create<Int>()
    private val habitFrequency = BehaviorSubject.create<Int>()
    private val habitDesiredIntensity = BehaviorSubject.create<Int>()
    private val habitIntensityFactorSubject = BehaviorSubject.create<Float>()
    private val setAsActiveSubject = BehaviorSubject.createDefault(false)
    private val createHabitSubject = PublishSubject.create<Unit>()

    override fun subscribeToView(view: CreateHabitView): Disposable = CompositeDisposable(
        currentStepSubject
            .scan(setOf<Step>()) { set, new -> set.plus(new) }
            .distinctUntilChanged()
            .observeOn(uiScheduler)
            .subscribe { steps -> view.handleCurrentStep(steps) },
        habitActionSubject
            .observeOn(uiScheduler)
            .subscribe {
                view.setAction(it)
                currentStepSubject.onNext(Step.SUBJECT)
            },
        habitTopicSubject
            .debounce(500L, TimeUnit.MILLISECONDS, computationScheduler)
            .observeOn(uiScheduler)
            .subscribe {
                if (it.isNotBlank())
                    currentStepSubject.onNext(Step.INTENSITY_BASE)
            },
        Observable.combineLatest(
            habitBaseIntensity,
            habitFrequency
        ) { intensity, frequency -> intensity to frequency }
            .debounce(500L, TimeUnit.MILLISECONDS, computationScheduler)
            .observeOn(uiScheduler)
            .subscribe { (intensity, frequency) ->
                currentStepSubject.onNext(Step.INTENSITY_DESIRED)
                view.setHabitFrequency(frequency)
            },
        habitDesiredIntensity
            .debounce(500L, TimeUnit.MILLISECONDS, computationScheduler)
            .observeOn(uiScheduler)
            .subscribe { currentStepSubject.onNext(Step.INTENSITY_FACTOR) },
        habitIntensityFactorSubject
            .debounce(500L, TimeUnit.MILLISECONDS, computationScheduler)
            .observeOn(uiScheduler)
            .subscribe { currentStepSubject.onNext(Step.ALLOW_CREATE) },
        createHabitSubject
            .throttleFirst(500L, TimeUnit.MILLISECONDS, computationScheduler)
            .switchMap {
                Observable.combineLatest(
                    habitActionSubject,
                    habitTopicSubject,
                    habitBaseIntensity,
                    habitFrequency,
                    habitDesiredIntensity,
                    habitIntensityFactorSubject,
                    setAsActiveSubject
                ) { action, subject, baseIntensity, frequency, desiredIntensity, factor, active ->
                    habitRepository.createHabit(
                        habitTopic = action,
                        habitSubject = subject,
                        baseIntensity = baseIntensity,
                        frequency = frequency,
                        desiredIntensity = desiredIntensity,
                        intensityFactor = factor,
                        setAsActive = active
                    )
                }
                    .firstElement()
                    .flatMapObservable {
                        it.toObservable()
                            .mapLeft { it as BaseEvent }
                            .startWithItem(LoadingEvent.left())
                    }
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
            }

    )

    fun actionEat() = habitActionSubject.onNext(HabitTopic.EAT)
    fun actionNotEat() = habitActionSubject.onNext(HabitTopic.NOT_EAT)
    fun actionTrain() = habitActionSubject.onNext(HabitTopic.TRAIN)
    fun actionStartDoing() = habitActionSubject.onNext(HabitTopic.START_DOING)
    fun actionStopDoing() = habitActionSubject.onNext(HabitTopic.STOP_DOING)
    fun setSubject(subject: String) = habitTopicSubject.onNext(subject)
    fun setBaseIntensity(baseIntensity: Int) = habitBaseIntensity.onNext(baseIntensity)
    fun setWeekly() = habitFrequency.onNext(WeeklyFlags.DURING_WEEK.bitFlag)
    fun setDaily() = habitFrequency.onNext(
        WeeklyFlags.MONDAY.bitFlag or
                WeeklyFlags.TUESDAY.bitFlag or
                WeeklyFlags.WEDNESDAY.bitFlag or
                WeeklyFlags.THURSDAY.bitFlag or
                WeeklyFlags.FRIDAY.bitFlag or
                WeeklyFlags.SATURDAY.bitFlag or
                WeeklyFlags.SUNDAY.bitFlag
    )

    fun setDesiredIntensity(intensity: Int) = habitDesiredIntensity.onNext(intensity)
    fun intensityFactor(factor: Float) = habitIntensityFactorSubject.onNext(factor)
    fun setAsActive(active: Boolean) = setAsActiveSubject.onNext(active)
    fun createHabit() = createHabitSubject.onNext(Unit)


    enum class Step {
        ACTIVITY, SUBJECT, INTENSITY_BASE, INTENSITY_DESIRED, INTENSITY_FACTOR, ALLOW_CREATE
    }
}