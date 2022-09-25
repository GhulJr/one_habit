package com.ghuljr.onehabit_presenter.add_action

import arrow.core.left
import arrow.core.right
import com.ghuljr.onehabit_data.repository.ActionsRepository
import com.ghuljr.onehabit_error.ValidationError
import com.ghuljr.onehabit_presenter.base.BasePresenter
import com.ghuljr.onehabit_tools.di.ActivityScope
import com.ghuljr.onehabit_tools.di.ComputationScheduler
import com.ghuljr.onehabit_tools.di.UiScheduler
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

    private val actionNameSubject = BehaviorSubject.createDefault("")
    private val createActionSubject = PublishSubject.create<Unit>()

    override fun subscribeToView(view: AddActionView): Disposable = CompositeDisposable(
        createActionSubject
            .throttleFirst(500L, TimeUnit.MILLISECONDS, computationScheduler)
            .withLatestFrom(actionNameSubject) { _, name -> if(name.isBlank()) ValidationError.EmptyField.left() else name.right() }
            .switchMapRightWithEither { name -> actionsRepository.createCustomAction()  }
            .observeOn(uiScheduler)
            .subscribe {

            }
    )

    fun setActionName(actionName: String) = actionNameSubject.onNext(actionName)
    fun createAction() = createActionSubject.onNext(Unit)
}