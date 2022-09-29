package com.ghuljr.onehabit_presenter.add_action

import arrow.core.*
import com.ghuljr.onehabit_data.repository.ActionsRepository
import com.ghuljr.onehabit_error.BaseEvent
import com.ghuljr.onehabit_error.LoadingEvent
import com.ghuljr.onehabit_error.ValidationError
import com.ghuljr.onehabit_presenter.base.BasePresenter
import com.ghuljr.onehabit_tools.di.ActivityScope
import com.ghuljr.onehabit_tools.di.ComputationScheduler
import com.ghuljr.onehabit_tools.di.UiScheduler
import com.ghuljr.onehabit_tools.extension.*
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
            .onlyDefined()
            .switchMapMaybe { actionsRepository.getActionObservable(it).firstElement() }
            .replay(1)
            .refCount()

        val itemsObservable = currentActionObservable
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
                            ) as BaseReminderItem
                        }
                            .let { if (it.size < action.totalRepeats) it.plus(AddReminderItem { addReminderClick() }) else it }
                    }
            }
            .replay(1)
            .refCount()

        return CompositeDisposable(
            createActionSubject
                .throttleFirst(500L, TimeUnit.MILLISECONDS, computationScheduler)
                .withLatestFrom(actionNameObservable, credentialsSubject) { _, name, initData -> if (name.isBlank()) (ValidationError.EmptyField as BaseEvent).left() else Triple(name, initData.first, initData.second).right() }
                .switchMapRightWithEither { (name, goalId, actionOption) ->
                    actionOption.fold(
                        ifSome = { actionsRepository.editCustomAction(name, goalId, it) },
                        ifEmpty = { actionsRepository.createCustomAction(name, goalId) }
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
            itemsObservable
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
    fun addReminder(time: Long) = addRemoveSubject.onNext(time to true)
    private fun addReminderClick() = addReminderClick.onNext(Unit)
}