package com.ghuljr.onehabit_presenter.main.today

import arrow.core.zip
import com.ghuljr.onehabit_data.domain.Action
import com.ghuljr.onehabit_data.domain.Habit
import com.ghuljr.onehabit_data.repository.ActionsRepository
import com.ghuljr.onehabit_data.repository.HabitRepository
import com.ghuljr.onehabit_data.repository.UserRepository
import com.ghuljr.onehabit_presenter.base.BasePresenter
import com.ghuljr.onehabit_tools.di.FragmentScope
import com.ghuljr.onehabit_tools.di.UiScheduler
import com.ghuljr.onehabit_tools.extension.mapRight
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.PublishSubject
import javax.inject.Inject

// TODO: Add option to schedule/change reminder notification for specific task on go.
@FragmentScope
class TodayPresenter @Inject constructor(
    private val actionsRepository: ActionsRepository,
    private val habitRepository: HabitRepository,
    private val userRepository: UserRepository,
    @UiScheduler private val uiScheduler: Scheduler
) : BasePresenter<TodayView>() {

    private val retrySubject = PublishSubject.create<Unit>()

    private fun todayActionsItemsObservable(view: TodayView) = Observable.combineLatest(
        actionsRepository.todayActionsObservable,
        habitRepository.todayHabitObservable,
        retrySubject
    ) { actionsEither, habitEither, _ -> actionsEither.zip(habitEither) }
        .mapRight { (actions, habit) ->
            val regularActions = actions.filterNot { it.finished || it.custom }
                .map { it.toRegularActionItem(view, habit) as TodayItem }
            val extraActions = actions.filter { it.custom && !it.finished }
                .map { it.toCustomActionItem(view, habit) as TodayItem }
            val finishedActions = actions.filter { it.finished }
                .map { it.toFinishedActionItem(view, habit) as TodayItem }

            regularActions
                .let { if(extraActions.isEmpty()) it else it.plus(AddActionItem { view.addCustomAction() }) }
                .plus(extraActions)
                .let { if (finishedActions.isEmpty()) it else it.plus(DoneActionsHeaderItem) }
                .plus(finishedActions)
        }
        .observeOn(uiScheduler)
        .share()


    override fun subscribeToView(view: TodayView): Disposable = CompositeDisposable(
        todayActionsItemsObservable(view).subscribe {
            it.fold(
                ifRight = { items -> view.submitItems(items) },
                ifLeft = { error -> view.handleItemsError(error) }
            )
        }
    )

    private fun Action.toRegularActionItem(view: TodayView, habit: Habit) = TodayActionItem(
        id = id,
        time = null, // TODO: handle time,
        quantity = currentRepeat?.let { current -> repeats?.let { max -> current to max } },
        onActionClick = { view.openDetails() },
        habitTopic = habit.type,
        habitSubject = habit.habitSubject,
        type = if (habit.settlingFormat <= 0) TodayActionItem.Type.WEEKLY else TodayActionItem.Type.DAILY
    )

    private fun Action.toCustomActionItem(view: TodayView, habit: Habit) = CustomActionItem(
        id = id,
        time = null, // TODO: handle time,
        onActionClick = { view.openDetails() },
        habitTopic = habit.type,
        habitSubject = habit.habitSubject
    )

    private fun Action.toFinishedActionItem(view: TodayView, habit: Habit) =
        TodayActionFinishedItem(
            id = id,
            time = null, // TODO: handle time,
            quantity = currentRepeat?.let { current -> repeats?.let { max -> current to max } },
            onActionClick = { view.openDetails() },
            habitTopic = habit.type,
            habitSubject = habit.habitSubject
        )
}