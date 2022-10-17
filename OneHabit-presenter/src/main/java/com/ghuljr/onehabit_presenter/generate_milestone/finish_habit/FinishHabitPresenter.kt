package com.ghuljr.onehabit_presenter.generate_milestone.finish_habit

import arrow.core.none
import arrow.core.some
import com.ghuljr.onehabit_data.repository.UserMetadataRepository
import com.ghuljr.onehabit_error.BaseEvent
import com.ghuljr.onehabit_presenter.base.BasePresenter
import com.ghuljr.onehabit_tools.di.ComputationScheduler
import com.ghuljr.onehabit_tools.di.FragmentScope
import com.ghuljr.onehabit_tools.di.UiScheduler
import com.ghuljr.onehabit_tools.extension.mapLeft
import com.ghuljr.onehabit_tools.extension.startWithLoading
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@FragmentScope
class FinishHabitPresenter @Inject constructor(
    @UiScheduler private val uiScheduler: Scheduler,
    @ComputationScheduler private val computationScheduler: Scheduler,
    private val userMetadataRepository: UserMetadataRepository
) : BasePresenter<FinishHabitView>() {

    private val addHabitStateSubject = BehaviorSubject.createDefault(false)
    private val finishSubject = PublishSubject.create<Unit>()

    override fun subscribeToView(view: FinishHabitView): Disposable = CompositeDisposable(
        finishSubject
            .throttleFirst(500L, TimeUnit.MILLISECONDS, computationScheduler)
            .withLatestFrom(addHabitStateSubject) { _, shouldAdd -> shouldAdd }
            .switchMap { shouldAdd ->
                userMetadataRepository.clearCurrentHabit(shouldAdd)
                    .toObservable()
                    .mapLeft { it as BaseEvent }
                    .startWithLoading()
            }
            .observeOn(uiScheduler)
            .subscribe {
                it.fold(
                    ifRight = {
                        view.handleEvent(none())
                        view.finish()
                    },
                    ifLeft = { view.handleEvent(it.some()) }
                )
            }
    )

    fun addHabitState(add: Boolean) = addHabitStateSubject.onNext(add)
    fun finish() = finishSubject.onNext(Unit)
}