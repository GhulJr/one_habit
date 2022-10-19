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
import kotlin.math.max
import kotlin.math.min

@FragmentScope
class ActionInfoPresenter @Inject constructor(
    @UiScheduler private val uiScheduler: Scheduler,
    @ComputationScheduler private val computationScheduler: Scheduler,
    private val actionsRepository: ActionsRepository,
    private val habitRepository: HabitRepository
) : BasePresenter<ActionInfoView>() {

    private val actionInfoSubject = PublishSubject.create<String>()
    private val completeActionStep = PublishSubject.create<Unit>()
    private val revertCompleteActionStep = PublishSubject.create<Unit>()
    private val removeActionSubject = PublishSubject.create<Unit>()
    private val editActionSubject = PublishSubject.create<Unit>()

    override fun subscribeToView(view: ActionInfoView): Disposable = CompositeDisposable(
        Observable.combineLatest(
            actionObservable,
            habitRepository.todayHabitObservable
        ) { actionEither, habitEither -> actionEither.zip(habitEither) }
            .mapLeft { it as BaseEvent }
            .mapRight { (action, habit) ->
                val type = if (habit.frequency <= 0) ActionType.WEEKLY else ActionType.DAILY
                val exceeded = action.run { repeatCount >= totalRepeats }
                ActionInfoItem(
                    customTitle = action.customTitle,
                    editable = action.customTitle != null && action.repeatCount < action.totalRepeats,
                    habitTopic = habit.topic,
                    quantity = if (action.totalRepeats <= 1) null else action.run { repeatCount.calculateCurrentRepeat(type == ActionType.WEEKLY, totalRepeats) to totalRepeats },
                    habitSubject = habit.habitSubject,
                    type = type,
                    reminders = action.reminders?.map { it.timeToString(TIME_FORMAT) },
                    exceeded = exceeded,
                    confirmAvailable = type == ActionType.WEEKLY && action.customTitle == null || !exceeded,
                    declineAvailable = action.repeatCount > 0
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
            .subscribe(),
        removeActionSubject
            .throttleFirst(500L, TimeUnit.MILLISECONDS, computationScheduler)
            .withLatestFrom(actionObservable) { _, action -> action }
            .switchMapRightWithEither { actionsRepository.removeAction(it).toObservable() }
            .observeOn(uiScheduler)
            .subscribe { view.close() },
        editActionSubject
            .throttleFirst(500L, TimeUnit.MILLISECONDS, computationScheduler)
            .withLatestFrom(actionObservable) { _, action -> action }
            .observeOn(uiScheduler)
            .subscribe { it.tap { view.editAction(it.goalId, it.id) } }

    )

    private val actionObservable = actionInfoSubject.switchMap { actionId ->
        actionsRepository.getActionObservable(actionId)
    }
        .replay(1)
        .refCount()


    fun completeActionStep() = completeActionStep.onNext(Unit)
    fun revertCompleteActionStep() = revertCompleteActionStep.onNext(Unit)

    fun removeAction() = removeActionSubject.onNext(Unit)
    fun editAction() = editActionSubject.onNext(Unit)

    fun displayAction(actionId: String) = actionInfoSubject.onNext(actionId)

    private fun Int.calculateCurrentRepeat(isWeekly: Boolean, totalRepeats: Int) = if (isWeekly) this else this + 1

    companion object {
        private const val TIME_FORMAT = "HH:mm"
    }
}