package com.ghuljr.onehabit_presenter.create_habit

import com.ghuljr.onehabit_data.repository.HabitRepository
import com.ghuljr.onehabit_presenter.base.BasePresenter
import com.ghuljr.onehabit_tools.di.ActivityScope
import com.ghuljr.onehabit_tools.di.UiScheduler
import com.ghuljr.onehabit_tools.model.HabitTopic
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import javax.inject.Inject

@ActivityScope
class CreateHabitPresenter @Inject constructor(
    @UiScheduler private val uiScheduler: Scheduler,
    private val habitRepository: HabitRepository
) : BasePresenter<CreateHabitView>() {

    private val currentStepSubject = BehaviorSubject.createDefault(Step.ACTIVITY)
    private val habitActionSubject = BehaviorSubject.create<HabitTopic>()

    override fun subscribeToView(view: CreateHabitView): Disposable = CompositeDisposable(
        currentStepSubject
            .reduce(Step.ACTIVITY to Step.ACTIVITY) { (_, old), new -> old to new }
            .filter { (old, new) -> Step.values().run { indexOf(old) < indexOf(new) } }
            .map { (_, new) -> new }
            .observeOn(uiScheduler)
            .subscribe { view.handleCurrentStep(it) },
        habitActionSubject
            .observeOn(uiScheduler)
            .subscribe {
                view.setAction(it)
                currentStepSubject.onNext(Step.SUBJECT)
            }
    )

    fun actionEat() = habitActionSubject.onNext(HabitTopic.EAT)
    fun actionNotEat() = habitActionSubject.onNext(HabitTopic.NOT_EAT)
    fun actionTrain() = habitActionSubject.onNext(HabitTopic.TRAIN)
    fun actionStartDoing() = habitActionSubject.onNext(HabitTopic.START_DOING)
    fun actionStopDoing() = habitActionSubject.onNext(HabitTopic.STOP_DOING)

    enum class Step {
        ACTIVITY, SUBJECT, INTENSITY_BASE, INTENSITY_DESIRED, FACTOR, SET_AS_ACTIVE, ALLOW_CREATE
    }
}