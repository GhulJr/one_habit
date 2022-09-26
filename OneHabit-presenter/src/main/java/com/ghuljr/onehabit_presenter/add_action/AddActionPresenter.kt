package com.ghuljr.onehabit_presenter.add_action

import arrow.core.left
import arrow.core.none
import arrow.core.right
import arrow.core.some
import com.ghuljr.onehabit_data.repository.ActionsRepository
import com.ghuljr.onehabit_error.BaseError
import com.ghuljr.onehabit_error.BaseEvent
import com.ghuljr.onehabit_error.LoadingEvent
import com.ghuljr.onehabit_error.ValidationError
import com.ghuljr.onehabit_presenter.base.BasePresenter
import com.ghuljr.onehabit_tools.di.ActivityScope
import com.ghuljr.onehabit_tools.di.ComputationScheduler
import com.ghuljr.onehabit_tools.di.UiScheduler
import com.ghuljr.onehabit_tools.extension.mapLeft
import com.ghuljr.onehabit_tools.extension.switchMapRightWithEither
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

    private val goalIdSubject = BehaviorSubject.createDefault("")
    private val createActionSubject = PublishSubject.create<Unit>()

    override fun subscribeToView(view: AddActionView): Disposable {
        val actionNameObservable = view.actionNameChangedObservable().replay(1).refCount()
        return CompositeDisposable(
            createActionSubject
                .map { println("TestClick1") }
                .throttleFirst(500L, TimeUnit.MILLISECONDS, computationScheduler)
                .withLatestFrom(actionNameObservable, goalIdSubject) { _, name, goalId -> if (name.isBlank()) (ValidationError.EmptyField as BaseEvent).left() else (name to goalId).right() }
                .switchMapRightWithEither { (name, goalId) ->
                    actionsRepository.createCustomAction(name, goalId)
                        .toObservable()
                        .mapLeft { it as BaseEvent }
                        .startWithItem(LoadingEvent.left())
                }
                .observeOn(uiScheduler)
                .subscribe {
                    println("TestClick4")
                    it.fold(
                        ifRight = {
                            view.handleEvent(none())
                            view.close()
                        },
                        ifLeft = { event -> view.handleEvent(event.some()) }
                    )
                },
            actionNameObservable.subscribe(),
        )

    }

    fun createAction() = createActionSubject.onNext(Unit)
    fun setGoalId(goalId: String) = goalIdSubject.onNext(goalId)
}