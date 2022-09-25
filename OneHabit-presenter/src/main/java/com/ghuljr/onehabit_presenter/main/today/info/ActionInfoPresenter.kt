package com.ghuljr.onehabit_presenter.main.today.info

import arrow.core.zip
import com.ghuljr.onehabit_data.repository.ActionsRepository
import com.ghuljr.onehabit_data.repository.HabitRepository
import com.ghuljr.onehabit_error.BaseEvent
import com.ghuljr.onehabit_presenter.base.BasePresenter
import com.ghuljr.onehabit_presenter.main.today.ActionInfoItem
import com.ghuljr.onehabit_presenter.main.today.ActionType
import com.ghuljr.onehabit_tools.di.ComputationScheduler
import com.ghuljr.onehabit_tools.di.FragmentScope
import com.ghuljr.onehabit_tools.di.UiScheduler
import com.ghuljr.onehabit_tools.extension.*
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@FragmentScope
// TODO: Allow to display specific buttons based on amount of items and type
class ActionInfoPresenter @Inject constructor(
    @UiScheduler private val uiScheduler: Scheduler,
    @ComputationScheduler private val computationScheduler: Scheduler,
    private val actionsRepository: ActionsRepository,
    private val habitRepository: HabitRepository
) : BasePresenter<ActionInfoView>() {

    private val actionInfoSubject = PublishSubject.create<String>()
    private val completeActionStep = PublishSubject.create<Unit>()
    private val revertCompleteActionStep = PublishSubject.create<Unit>()

    override fun subscribeToView(view: ActionInfoView): Disposable = CompositeDisposable(
        Observable.combineLatest(
            actionObservable,
            habitRepository.todayHabitObservable
        ) { actionEither, habitEither -> actionEither.zip(habitEither) }
            .mapLeft { it as BaseEvent }
            .mapRight { (action, habit) ->
                ActionInfoItem(
                    editable = action.custom,
                    habitTopic = habit.type,
                    quantity = action.repeatCount?.let { current -> action.totalRepeats?.let { max -> current.calculateCurrentRepeat(habit.settlingFormat <= 0) to max } },
                    habitSubject = habit.habitSubject,
                    type = if (habit.settlingFormat <= 0) ActionType.WEEKLY else ActionType.DAILY,
                    reminders = action.reminders?.map { it.timeToString(TIME_FORMAT) },
                    exceeded = action.run { repeatCount != null && totalRepeats != null && repeatCount!! >= totalRepeats!! }
                )
            }

            .onlyRight()
            .observeOn(uiScheduler)
            .subscribe { actionInfo -> view.displayActionInfo(actionInfo) },

        Observable.merge(
            completeActionStep
                .throttleFirst(500L, TimeUnit.MILLISECONDS, computationScheduler)
                .withLatestFrom(actionObservable) { _, action -> action }
                .switchMapRightWithEither { action ->
                    actionsRepository.completeAction(action.id, action.goalId)
                        .toObservable()
                },
            revertCompleteActionStep
                .throttleFirst(500L, TimeUnit.MILLISECONDS, computationScheduler)
                .withLatestFrom(actionObservable) { _, action -> action }
                .switchMapRightWithEither { action ->
                    actionsRepository.revertCompleteAction(action.id, action.goalId)
                        .toObservable()
                }
        )
            .observeOn(uiScheduler)
            .subscribe {
                it.fold(
                    ifRight = { /* TODO: is any action required here? */ },
                    ifLeft = { /* TODO: handle error and loading */ }
                )
            }

    )

    private val actionObservable = actionInfoSubject.switchMap { actionId ->
        actionsRepository.getActionObservable(actionId)
    }
        .replay(1)
        .refCount()


    fun completeActionStep() = completeActionStep.onNext(Unit)
    fun revertCompleteActionStep() = revertCompleteActionStep.onNext(Unit)

    fun displayAction(actionId: String) = actionInfoSubject.onNext(actionId)

    private fun Int.calculateCurrentRepeat(exceeded: Boolean) = if (exceeded) this else this + 1

    companion object {
        private const val TIME_FORMAT = "HH:mm"
    }
}