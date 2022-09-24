package com.ghuljr.onehabit_presenter.main.today

import arrow.core.Either
import arrow.core.left
import arrow.core.zip
import com.ghuljr.onehabit_data.domain.Action
import com.ghuljr.onehabit_data.domain.Habit
import com.ghuljr.onehabit_data.repository.ActionsRepository
import com.ghuljr.onehabit_data.repository.HabitRepository
import com.ghuljr.onehabit_error.BaseError
import com.ghuljr.onehabit_error.BaseEvent
import com.ghuljr.onehabit_error.LoadingEvent
import com.ghuljr.onehabit_presenter.base.BasePresenter
import com.ghuljr.onehabit_tools.di.FragmentScope
import com.ghuljr.onehabit_tools.di.UiScheduler
import com.ghuljr.onehabit_tools.extension.mapLeft
import com.ghuljr.onehabit_tools.extension.mapRight
import com.ghuljr.onehabit_tools.extension.timeToString
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

// TODO: list finished items, that are on done state
// TODO: make swipe refresh
// TODO: handle loading and error
// TODO: handle displaying details and confirming
// TODO: handle adding and editing custom action
// TODO: Add option to schedule/change reminder notification for specific task on go.
@FragmentScope
class TodayPresenter @Inject constructor(
    private val actionsRepository: ActionsRepository,
    private val habitRepository: HabitRepository,
    @UiScheduler private val uiScheduler: Scheduler
) : BasePresenter<TodayView>() {

    private val retrySubject = PublishSubject.create<Unit>()
    private val selectItemSubject = PublishSubject.create<String>()

    private fun todayActionsItemsObservable(view: TodayView): Observable<Either<BaseEvent, List<TodayItem>>> =
        retrySubject
            .startWithItem(Unit)
            .switchMap {
                Observable.combineLatest(
                    actionsRepository.todayActionsObservable,
                    habitRepository.todayHabitObservable
                ) { actionsEither, habitEither -> actionsEither.zip(habitEither) }
            }
            .mapLeft { it as BaseEvent }
            .mapRight { (actions, habit) ->
                val regularActions = actions
                    .filterNot { it.finished || it.custom }
                    .map { it.toRegularActionItem(habit) as TodayItem }
                val extraActions = actions
                    .filter { it.custom && !it.finished }
                    .map { it.toCustomActionItem(habit) as TodayItem }
                val finishedActions = actions
                    .filter { it.finished }
                    .map { it.toFinishedActionItem(habit) as TodayItem }

                regularActions
                    .let { if (extraActions.isEmpty()) it.plus(AddActionItem { view.addCustomAction() }) else it }
                    .plus(extraActions)
                    .let { if (finishedActions.isEmpty()) it else it.plus(DoneActionsHeaderItem) }
                    .plus(finishedActions)
            }
            .startWithItem(LoadingEvent.left())
            .observeOn(uiScheduler)
            .share()


    override fun subscribeToView(view: TodayView): Disposable = CompositeDisposable(
        todayActionsItemsObservable(view)
            .subscribe {
                it.fold(
                    ifRight = { items -> view.submitItems(items) },
                    ifLeft = { event ->
                        if (event is BaseError)
                            view.handleItemsError(event)
                    }
                )
                view.handleLoading(it.swap().orNull() is LoadingEvent)
            },
        selectItemSubject
            .throttleFirst(500L, TimeUnit.MILLISECONDS, uiScheduler)
            .subscribe { actionId -> view.openDetails(actionId) }
    )

    private fun selectItem(actionId: String) = selectItemSubject.onNext(actionId)

    private fun Action.toRegularActionItem(habit: Habit) = TodayActionItem(
        id = id,
        time = reminders?.getOrNull(maxOf((currentRepeat ?: Int.MAX_VALUE) - 1, 0))
            ?.timeToString(TIME_FORMAT),
        quantity = currentRepeat?.let { current -> repeats?.let { max -> current to max } },
        onActionClick = { selectItem(id) },
        habitTopic = habit.type,
        habitSubject = habit.habitSubject,
        type = if (habit.settlingFormat <= 0) TodayActionItem.Type.WEEKLY else TodayActionItem.Type.DAILY
    )

    private fun Action.toCustomActionItem(habit: Habit) = CustomActionItem(
        id = id,
        time = reminders?.getOrNull(maxOf((currentRepeat ?: Int.MAX_VALUE) - 1, 0))
            ?.timeToString(TIME_FORMAT),
        onActionClick = { selectItem(id) },
        habitTopic = habit.type,
        habitSubject = habit.habitSubject
    )

    private fun Action.toFinishedActionItem(habit: Habit) =
        TodayActionFinishedItem(
            id = id,
            time = reminders?.getOrNull(maxOf((currentRepeat ?: Int.MAX_VALUE) - 1, 0))
                ?.timeToString(TIME_FORMAT),
            quantity = currentRepeat?.let { current -> repeats?.let { max -> calculateCurrentRepeat(current) to max } },
            onActionClick = { selectItem(id) },
            habitTopic = habit.type,
            habitSubject = habit.habitSubject
        )

    private fun calculateCurrentRepeat(currentRepeat: Int) = currentRepeat + 1

    companion object {
        private const val TIME_FORMAT = "HH:mm"
    }
}