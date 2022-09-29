package com.ghuljr.onehabit_presenter.add_action

import arrow.core.*
import com.ghuljr.onehabit_data.repository.ActionsRepository
import com.ghuljr.onehabit_error.BaseEvent
import com.ghuljr.onehabit_error.LoadingEvent
import com.ghuljr.onehabit_error.NoDataError
import com.ghuljr.onehabit_error.ValidationError
import com.ghuljr.onehabit_presenter.base.BasePresenter
import com.ghuljr.onehabit_tools.di.ActivityScope
import com.ghuljr.onehabit_tools.di.ComputationScheduler
import com.ghuljr.onehabit_tools.di.UiScheduler
import com.ghuljr.onehabit_tools.extension.*
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@ActivityScope
class AddActionPresenter @Inject constructor(
    @UiScheduler private val uiScheduler: Scheduler,
    @ComputationScheduler private val computationScheduler: Scheduler,
    private val actionsRepository: ActionsRepository
) : BasePresenter<AddActionView>() {

    private val credentialsSubject = BehaviorSubject.createDefault("" to none<String>())
    private val createActionSubject = PublishSubject.create<Unit>()
    private val addRemoveSubject = PublishSubject.create<Pair<Long, Boolean>>()
    private val addReminderClick = PublishSubject.create<Unit>()

    override fun subscribeToView(view: AddActionView): Disposable {
        val actionNameObservable = view.actionNameChangedObservable().replay(1).refCount()
        val currentActionObservable = credentialsSubject
            .map { it.second }
            .take(1)
            .switchMapMaybe {
                it.fold(
                    ifSome = { actionsRepository.getActionObservable(it).firstElement() },
                    ifEmpty = { Maybe.just(NoDataError.left()) }
                )
            }
            .replay(1)
            .refCount()

        val remindersObservable = currentActionObservable
            .onlyRight()
            .switchMap { action ->
                addRemoveSubject
                    .scan(action.reminders ?: listOf()) { acc, (time, removeOrAdd) ->
                        if (removeOrAdd)
                            acc.plus(time)
                        else
                            acc.filter { it == time }
                    }
                    .map { it.sorted() }
                    .map {
                        it.map { time ->
                            ReminderItem(
                                time = time,
                                removeClick = { removeReminder(time) }
                            )
                        } to (it.size < action.totalRepeats)
                    }
            }
            .replay(1)
            .refCount()

        return CompositeDisposable(
            createActionSubject
                .throttleFirst(500L, TimeUnit.MILLISECONDS, computationScheduler)
                .withLatestFrom(actionNameObservable, credentialsSubject, currentActionObservable, remindersObservable.map { (items, _) -> items.map { it.time } }) { _, name, initData, action, reminders -> if (name.isBlank()) (ValidationError.EmptyField as BaseEvent).left() else Tuple4(name, initData.first, action, reminders).right() }
                .switchMapRightWithEither { (name, goalId, actionEither, reminders) ->
                    actionEither.fold(
                        ifRight = {
                            if (it.customTitle == null)
                                actionsRepository.addRemindersToRegularAction(
                                    goalId,
                                    it.id,
                                    reminders
                                )
                            else
                                actionsRepository.editCustomAction(
                                    actionName = name,
                                    goalId = goalId,
                                    actionId = it.id,
                                    reminders = reminders
                                )

                        },
                        ifLeft = { actionsRepository.createCustomAction(name, goalId, reminders) }
                    )
                        .toObservable()
                        .mapLeft { it as BaseEvent }
                        .startWithItem(LoadingEvent.left())
                }
                .observeOn(uiScheduler)
                .subscribe {
                    it.fold(
                        ifRight = {
                            view.handleEvent(none())
                            view.close()
                        },
                        ifLeft = { event -> view.handleEvent(event.some()) }
                    )
                },
            currentActionObservable
                .observeOn(uiScheduler)
                .onlyRight()
                .subscribe {
                    view.setActionTitle(it.customTitle ?: "")
                    view.enableSetTitle(it.customTitle != null)
                },
            actionNameObservable.subscribe(),
            remindersObservable
                .map { (items, allowAdd) ->
                    items.map { it as BaseReminderItem }
                        .let {
                            if (allowAdd)
                                it.plus(AddReminderItem { addReminderClick() })
                            else
                                it
                        }

                }
                .observeOn(uiScheduler)
                .subscribe { view.setReminders(it) },
            addReminderClick
                .throttleFirst(500L, TimeUnit.MILLISECONDS, computationScheduler)
                .observeOn(uiScheduler)
                .subscribe {
                    view.openDatePicker()
                }
        )
    }

    fun createAction() = createActionSubject.onNext(Unit)
    fun init(initData: Pair<String, Option<String>>) = credentialsSubject.onNext(initData)

    private fun removeReminder(time: Long) = addRemoveSubject.onNext(time to false)
    fun addReminder(hours: Int, minutes: Int) = addRemoveSubject.onNext(hours.toLong() * HOUR_AS_MILLIS + minutes * MINUTE_AS_MILLIS to true)
    private fun addReminderClick() = addReminderClick.onNext(Unit)

    companion object {
        private const val MINUTE_AS_MILLIS = 60 * 1000
        private const val HOUR_AS_MILLIS = 60 * MINUTE_AS_MILLIS
    }
}