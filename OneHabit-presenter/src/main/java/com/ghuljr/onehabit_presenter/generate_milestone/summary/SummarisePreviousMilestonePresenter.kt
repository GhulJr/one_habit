package com.ghuljr.onehabit_presenter.generate_milestone.summary

import arrow.core.none
import arrow.core.some
import com.ghuljr.onehabit_data.repository.MilestoneRepository
import com.ghuljr.onehabit_error.BaseEvent
import com.ghuljr.onehabit_presenter.base.BasePresenter
import com.ghuljr.onehabit_tools.di.ComputationScheduler
import com.ghuljr.onehabit_tools.di.FragmentScope
import com.ghuljr.onehabit_tools.di.UiScheduler
import com.ghuljr.onehabit_tools.extension.mapLeft
import com.ghuljr.onehabit_tools.extension.mapRight
import com.ghuljr.onehabit_tools.extension.startWithLoading
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@FragmentScope
class SummarisePreviousMilestonePresenter @Inject constructor(
    @ComputationScheduler private val computationScheduler: Scheduler,
    @UiScheduler private val uiScheduler: Scheduler,
    private val milestoneRepository: MilestoneRepository
) : BasePresenter<SummarisePreviousMilestoneView>() {

    private val goNextSubject = PublishSubject.create<Unit>()

    override fun subscribeToView(view: SummarisePreviousMilestoneView): Disposable =
        CompositeDisposable(
            goNextSubject
                .throttleFirst(500L, TimeUnit.MILLISECONDS, computationScheduler)
                .switchMap {
                    milestoneRepository.currentMilestoneObservable
                        .mapRight { it.intensity >= 100 }
                        .firstElement()
                        .toObservable()
                        .mapLeft { it as BaseEvent }
                        .startWithLoading()
                }
                .observeOn(uiScheduler)
                .subscribe {
                    it.fold(
                        ifRight = { isHabitFinished ->
                            view.handleEvent(none())
                            if (isHabitFinished) view.finishHabit()
                            else view.generateNewMilestone()
                        },
                        ifLeft = { view.handleEvent(it.some()) }
                    )
                }
        )

    fun goNext() = goNextSubject.onNext(Unit)
}