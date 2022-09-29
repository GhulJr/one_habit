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
import com.ghuljr.onehabit_tools.extension.mapLeft
import com.ghuljr.onehabit_tools.extension.onlyDefined
import com.ghuljr.onehabit_tools.extension.onlyRight
import com.ghuljr.onehabit_tools.extension.switchMapRightWithEither
import io.reactivex.rxjava3.core.Observable
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

    override fun subscribeToView(view: AddActionView): Disposable {
        val actionNameObservable = view.actionNameChangedObservable().replay(1).refCount()
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
            credentialsSubject
                .map { it.second }
                .take(1)
                .onlyDefined()
                .switchMapMaybe { actionsRepository.getActionObservable(it).firstElement() }
                .observeOn(uiScheduler)
                .onlyRight()
                .subscribe {
                    view.setActionTitle(it.customTitle ?: "")
                    view.enableSetTitle(it.customTitle != null)
                },
            actionNameObservable.subscribe(),
        )
    }

    fun createAction() = createActionSubject.onNext(Unit)
    fun init(initData: Pair<String, Option<String>>) = credentialsSubject.onNext(initData)
}