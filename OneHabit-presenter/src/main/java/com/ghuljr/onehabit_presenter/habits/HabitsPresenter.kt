package com.ghuljr.onehabit_presenter.habits

import arrow.core.none
import arrow.core.some
import com.ghuljr.onehabit_data.domain.Habit
import com.ghuljr.onehabit_data.repository.HabitRepository
import com.ghuljr.onehabit_presenter.base.BasePresenter
import com.ghuljr.onehabit_tools.di.ActivityScope
import com.ghuljr.onehabit_tools.di.ComputationScheduler
import com.ghuljr.onehabit_tools.di.UiScheduler
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@ActivityScope
class HabitsPresenter @Inject constructor(
    @UiScheduler private val uiScheduler: Scheduler,
    @ComputationScheduler private val computationScheduler: Scheduler,
    private val habitRepository: HabitRepository
) : BasePresenter<HabitsView>() {

    private val openHabitSubject = PublishSubject.create<String>()

    override fun subscribeToView(view: HabitsView): Disposable = CompositeDisposable(
        habitRepository.allHabitsObservable
            .observeOn(uiScheduler)
            .subscribe {
                it.fold(
                    ifRight = {
                        view.handleEvent(none())
                        view.submitList(it.toItems())
                    },
                    ifLeft = { view.handleEvent(it.some()) }
                )
            },
        openHabitSubject
            .throttleFirst(500L, TimeUnit.MILLISECONDS, computationScheduler)
            .observeOn(uiScheduler)
            .subscribe { view.openHabitDetails(it) }
    )

    private fun List<Habit>.toItems() = map { it.toItem() }

    private fun Habit.toItem() = HabitItem(
        id = id,
        habitTopic = topic,
        habitSubject = habitSubject,
        onClick = { openHabitClick(id) }
    )

    private fun openHabitClick(id: String) = openHabitSubject.onNext(id)
}