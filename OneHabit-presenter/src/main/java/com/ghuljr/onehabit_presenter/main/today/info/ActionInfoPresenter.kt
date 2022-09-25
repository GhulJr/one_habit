package com.ghuljr.onehabit_presenter.main.today.info

import arrow.core.zip
import com.ghuljr.onehabit_data.repository.ActionsRepository
import com.ghuljr.onehabit_data.repository.HabitRepository
import com.ghuljr.onehabit_error.BaseEvent
import com.ghuljr.onehabit_presenter.base.BasePresenter
import com.ghuljr.onehabit_presenter.main.today.ActionInfoItem
import com.ghuljr.onehabit_presenter.main.today.ActionType
import com.ghuljr.onehabit_tools.di.FragmentScope
import com.ghuljr.onehabit_tools.di.UiScheduler
import com.ghuljr.onehabit_tools.extension.mapLeft
import com.ghuljr.onehabit_tools.extension.mapRight
import com.ghuljr.onehabit_tools.extension.onlyRight
import com.ghuljr.onehabit_tools.extension.timeToString
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.PublishSubject
import javax.inject.Inject

@FragmentScope
class ActionInfoPresenter @Inject constructor(
    @UiScheduler private val uiScheduler: Scheduler,
    private val actionsRepository: ActionsRepository,
    private val habitRepository: HabitRepository
) : BasePresenter<ActionInfoView>() {

    private val actionInfoSubject = PublishSubject.create<String>()

    override fun subscribeToView(view: ActionInfoView): Disposable = CompositeDisposable(
        actionInfoSubject
            .switchMap { actionId ->
                Observable.combineLatest(
                    actionsRepository.getActionObservable(actionId),
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
            }
            .onlyRight()
            .observeOn(uiScheduler)
            .subscribe { actionInfo -> view.displayActionInfo(actionInfo) }

    )

    fun displayAction(actionId: String) = actionInfoSubject.onNext(actionId)

    private fun Int.calculateCurrentRepeat(exceeded: Boolean) = if (exceeded) this else this + 1

    companion object {
        private const val TIME_FORMAT = "HH:mm"
    }
}