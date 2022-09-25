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
import com.ghuljr.onehabit_error.UnknownError
import com.ghuljr.onehabit_presenter.base.BasePresenter
import com.ghuljr.onehabit_tools.di.ComputationScheduler
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
import java.lang.RuntimeException
import java.util.concurrent.TimeUnit
import javax.inject.Inject

// TODO: handle adding and editing custom action
// TODO: Add option to schedule/change reminder notification for specific task on go.
@FragmentScope
class TodayPresenter @Inject constructor(
    private val actionsRepository: ActionsRepository,
    private val habitRepository: HabitRepository,
    @UiScheduler private val uiScheduler: Scheduler,
    @ComputationScheduler private val computationScheduler: Scheduler
) : BasePresenter<TodayView>() {

    private val refreshSubject = PublishSubject.create<Unit>()
    private val selectItemSubject = PublishSubject.create<String>()

    override fun subscribeToView(view: TodayView): Disposable = CompositeDisposable(
        todayActionsItemsObservable(view)
            .subscribe {
                view.handleLoading(it.swap().orNull() is LoadingEvent)
                it.fold(
                    ifRight = { items ->
                        view.submitItems(items)
                        view.handleItemsError(null)
                    },
                    ifLeft = { event ->
                        if (event is BaseError)
                            view.handleItemsError(event)
                        else
                            view.handleItemsError(null)
                    }
                )
                view.handleLoading(it.swap().orNull() is LoadingEvent)
            },
        selectItemSubject
            .throttleFirst(500L, TimeUnit.MILLISECONDS, computationScheduler)
            .observeOn(uiScheduler)
            .subscribe { actionId -> view.openDetails(actionId) },
        refreshSubject
            .throttleFirst(500L, TimeUnit.MILLISECONDS, computationScheduler)
            .switchMap {
                actionsRepository.refreshTodayActions()
                    .toObservable()
                    .mapLeft { it as BaseEvent }
                    .startWithItem(LoadingEvent.left())
            }
            .observeOn(uiScheduler)
            .subscribe {
                view.handleLoading(it.swap().orNull() is LoadingEvent)
                it.fold(
                    ifRight = { view.handleItemsError(null) },
                    ifLeft = { event ->
                        if (event is BaseError)
                            view.handleItemsError(event)
                        else
                            view.handleItemsError(null)
                    }
                )
            }
    )

    private fun todayActionsItemsObservable(view: TodayView): Observable<Either<BaseEvent, List<TodayItem>>> =
        Observable.combineLatest(
            actionsRepository.todayActionsObservable,
            habitRepository.todayHabitObservable
        ) { actionsEither, habitEither -> actionsEither.zip(habitEither) }
            .mapLeft { it as BaseEvent }
            .mapRight { (actions, habit) ->
                val regularActions = actions
                    .filterNot { it.custom }
                    .filter { it.repeatCount < it.totalRepeats || habit.settlingFormat == 0 }
                    .map { it.toRegularActionItem(habit) as TodayItem }
                val extraActions = actions
                    .filter { it.custom && it.repeatCount < it.totalRepeats }
                    .map { it.toCustomActionItem(habit) as TodayItem }
                val finishedActions = actions
                    .filter { it.repeatCount >= it.totalRepeats && (habit.settlingFormat != 0 || it.custom) }
                    .map { it.toFinishedActionItem(habit) as TodayItem }

                regularActions
                    .let { if (extraActions.isEmpty()) it.plus(AddActionItem { addCustomAction() }) else it }
                    .plus(extraActions)
                    .let { if (finishedActions.isEmpty()) it else it.plus(DoneActionsHeaderItem) }
                    .plus(finishedActions)
            }
            .startWithItem(LoadingEvent.left())
            .observeOn(uiScheduler)
            .share()

    fun refresh() = refreshSubject.onNext(Unit)

    private fun addCustomAction() {}

    private fun selectItem(actionId: String) = selectItemSubject.onNext(actionId)

    private fun Action.toRegularActionItem(habit: Habit) = TodayActionItem(
        id = id,
        time = reminders?.getOrNull(repeatCount)?.timeToString(TIME_FORMAT),
        quantity = if (totalRepeats <= 1) null else repeatCount.calculateCurrentRepeat(habit.settlingFormat <= 0) to totalRepeats,
        onActionClick = { selectItem(id) },
        habitTopic = habit.type,
        habitSubject = habit.habitSubject,
        actionType = if (habit.settlingFormat <= 0) ActionType.WEEKLY else ActionType.DAILY,
        exceeded = repeatCount >= totalRepeats
    )

    private fun Action.toCustomActionItem(habit: Habit) = CustomActionItem(
        id = id,
        time = reminders?.getOrNull(repeatCount)?.timeToString(TIME_FORMAT),
        onActionClick = { selectItem(id) },
        habitTopic = habit.type,
        habitSubject = habit.habitSubject
    )

    private fun Action.toFinishedActionItem(habit: Habit) =
        TodayActionFinishedItem(
            id = id,
            time = reminders?.getOrNull(repeatCount)?.timeToString(TIME_FORMAT),
            quantity = if (totalRepeats <= 1) null else repeatCount.calculateCurrentRepeat(false) to totalRepeats,
            onActionClick = { selectItem(id) },
            habitTopic = habit.type,
            habitSubject = habit.habitSubject
        )

    private fun Int.calculateCurrentRepeat(isWeekly: Boolean) = if (isWeekly) this else this + 1

    companion object {
        private const val TIME_FORMAT = "HH:mm"
    }
}