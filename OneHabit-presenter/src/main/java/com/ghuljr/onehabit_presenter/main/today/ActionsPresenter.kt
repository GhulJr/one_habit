package com.ghuljr.onehabit_presenter.main.today

import arrow.core.Either
import arrow.core.Option
import arrow.core.left
import arrow.core.zip
import com.ghuljr.onehabit_data.domain.Action
import com.ghuljr.onehabit_data.domain.Habit
import com.ghuljr.onehabit_data.repository.ActionsRepository
import com.ghuljr.onehabit_data.repository.HabitRepository
import com.ghuljr.onehabit_data.repository.UserMetadataRepository
import com.ghuljr.onehabit_error.BaseError
import com.ghuljr.onehabit_error.BaseEvent
import com.ghuljr.onehabit_error.LoadingEvent
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
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

// TODO: Add option to schedule/change reminder notification for specific task on go.
// TODO: display placeholder when there are no actions displayed
@FragmentScope
class ActionsPresenter @Inject constructor(
    private val actionsRepository: ActionsRepository,
    private val habitRepository: HabitRepository,
    private val userMetadataRepository: UserMetadataRepository,
    @UiScheduler private val uiScheduler: Scheduler,
    @ComputationScheduler private val computationScheduler: Scheduler
) : BasePresenter<ActionsView>() {

    private val refreshSubject = PublishSubject.create<Unit>()
    private val selectItemSubject = PublishSubject.create<String>()
    private val addCustomActionSubject = PublishSubject.create<Unit>()
    private val initSubject = BehaviorSubject.create<Option<String>>()

    override fun subscribeToView(view: ActionsView): Disposable = CompositeDisposable(
        initSubject
            .take(1)
            .switchMap {
                actionsObservable(
                    it.fold(
                        ifSome = { actionsRepository.actionsByGoalId(it) },
                        ifEmpty = { actionsRepository.todayActionsObservable }
                    )
                )
            }
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
            },
        addCustomActionSubject
            .throttleFirst(500L, TimeUnit.MILLISECONDS, computationScheduler)
            .switchMapMaybe { userMetadataRepository.currentUser.firstElement() }
            .observeOn(uiScheduler)
            .subscribe {
                it.fold(
                    ifRight = { user -> view.openCreateCustomAction(user.goalId!!) },
                    ifLeft = { error -> view.handleItemsError(error) }
                )
            },
    )

    private fun actionsObservable(actionsSourceObservable: Observable<Either<BaseError, List<Action>>>): Observable<Either<BaseEvent, List<TodayItem>>> =
        Observable.combineLatest(
            actionsSourceObservable,
            habitRepository.todayHabitObservable
        ) { actionsEither, habitEither -> actionsEither.zip(habitEither) }
            .mapLeft { it as BaseEvent }
            .mapRight { (actions, habit) ->
                val regularActions = actions
                    .filter { it.customTitle == null }
                    .filter { it.repeatCount < it.totalRepeats || habit.settlingFormat == 0 }
                    .map { it.toRegularActionItem(habit) as TodayItem }
                val extraActions = actions
                    .filter { it.customTitle != null && it.repeatCount < it.totalRepeats }
                    .map { it.toCustomActionItem(habit) as TodayItem }
                val finishedActions = actions
                    .filter { it.repeatCount >= it.totalRepeats && (habit.settlingFormat != 0 || it.customTitle != null) }
                    .map { it.toFinishedActionItem(habit) as TodayItem }

                regularActions
                    .let { if (actions.none { it.customTitle != null }) it.plus(AddActionItem { addCustomAction() }) else it }
                    .plus(extraActions)
                    .let { if (finishedActions.isEmpty()) it else it.plus(DoneActionsHeaderItem) }
                    .plus(finishedActions)
            }
            .startWithItem(LoadingEvent.left())
            .observeOn(uiScheduler)
            .share()

    fun refresh() = refreshSubject.onNext(Unit)
    fun init(goalId: Option<String>) = initSubject.onNext(goalId)

    private fun addCustomAction() = addCustomActionSubject.onNext(Unit)

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
        habitSubject = habit.habitSubject,
        title = customTitle!!
    )

    private fun Action.toFinishedActionItem(habit: Habit) =
        TodayActionFinishedItem(
            id = id,
            time = reminders?.getOrNull(repeatCount)?.timeToString(TIME_FORMAT),
            quantity = if (totalRepeats <= 1) null else repeatCount.calculateCurrentRepeat(false) to totalRepeats,
            onActionClick = { selectItem(id) },
            habitTopic = habit.type,
            habitSubject = habit.habitSubject,
            customTitle = customTitle
        )

    private fun Int.calculateCurrentRepeat(isWeekly: Boolean) = if (isWeekly) this else this + 1

    companion object {
        private const val TIME_FORMAT = "HH:mm"
    }
}