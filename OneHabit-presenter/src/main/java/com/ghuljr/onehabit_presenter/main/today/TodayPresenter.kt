package com.ghuljr.onehabit_presenter.main.today

import arrow.core.computations.ensureNotNull
import arrow.core.computations.nullable
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
import javax.inject.Inject

// TODO: Add option to schedule/change reminder notification for specific task on go.
@FragmentScope
class TodayPresenter @Inject constructor(
    private val actionsRepository: ActionsRepository,
    private val habitRepository: HabitRepository,
    private val userRepository: UserRepository,
    @UiScheduler private val uiScheduler: Scheduler
) : BasePresenter<TodayView>() {

    private fun todayActionsItemsObservable(view: TodayView) = Observable.combineLatest(
        actionsRepository.todayActionsObservable,
        habitRepository.todayHabitObservable
    ) { actionsEither, habitEither -> actionsEither.zip(habitEither) }
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

    override fun subscribeToView(view: TodayView): Disposable = CompositeDisposable(


    )

    /*
    * If it is weekly, then hold one action for whole week and do not move it do "Finished". Instead when the limit is down,
    * Replace count and title with "Do not do! when it exceed". Show "This week" if there is no timer scheduled.
    *
    * If there are days, then it should show daily counts as current item as long as it has scheduled reminder, "Today otherwise"
    *
    *
    * */
    private fun getFakeData(view: TodayView): List<TodayItem> = listOf(
        TodayActionItem(
            title = "Train hard!",
            time = "10:20",
            quantity = 2 to 3,
            onActionClick = { view.openDetails() }
        ),
        TodayActionItem(
            title = "Train hard!",
            time = "20:20",
            quantity = 3 to 3,
            onActionClick = { view.openDetails() }
        ),
        TodayActionItem(
            title = "Chocolate no!",
            time = null,
            quantity = null,
            onActionClick = { view.openDetails() }
        ),
        AddActionItem { view.addCustomAction() },
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