package com.ghuljr.onehabit_presenter.generate_milestone.intro

import arrow.core.getOrElse
import arrow.core.none
import arrow.core.some
import arrow.core.zip
import com.ghuljr.onehabit_data.repository.MilestoneRepository
import com.ghuljr.onehabit_data.repository.UserMetadataRepository
import com.ghuljr.onehabit_error.BaseEvent
import com.ghuljr.onehabit_presenter.base.BasePresenter
import com.ghuljr.onehabit_tools.di.ComputationScheduler
import com.ghuljr.onehabit_tools.di.FragmentScope
import com.ghuljr.onehabit_tools.di.UiScheduler
import com.ghuljr.onehabit_tools.extension.mapLeft
import com.ghuljr.onehabit_tools.extension.mapRight
import com.ghuljr.onehabit_tools.extension.startWithLoading
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@FragmentScope
class MilestoneIntroPresenter @Inject constructor(
    @UiScheduler private val uiScheduler: Scheduler,
    @ComputationScheduler private val computationScheduler: Scheduler,
    private val milestoneRepository: MilestoneRepository,
    private val userMetadataRepository: UserMetadataRepository
) : BasePresenter<MilestoneIntroView>() {

    private val nextSubject = PublishSubject.create<Unit>()

    override fun subscribeToView(view: MilestoneIntroView): Disposable = CompositeDisposable(
        nextSubject
            .throttleFirst(500L, TimeUnit.MILLISECONDS, computationScheduler)
            .switchMap {
                Observable.combineLatest(
                    milestoneRepository.currentMilestoneObservable.map { it.map { it.intensity >= 100 }.getOrElse { false } },
                    userMetadataRepository.currentUser
                ) { habitFinished, userEither -> userEither.map { it to habitFinished } }
                    .mapRight { (user, habitFinished) ->
                        when {
                            habitFinished -> NextStep.FINISH_HABIT
                            user.milestoneId == null -> NextStep.GENERATE
                            else -> NextStep.SUMMARY
                        }
                    }
                    .firstElement()
                    .toObservable()
                    .mapLeft { it as BaseEvent }
                    .startWithLoading()
            }
            .observeOn(uiScheduler)
            .subscribe {
                it.fold(
                    ifRight = { nextStep ->
                        view.handleEvent(none())
                        view.goNextStep(nextStep)
                    },
                    ifLeft = { view.handleEvent(it.some()) }
                )
            }
    )

    fun next() = nextSubject.onNext(Unit)

    enum class NextStep {
        SUMMARY, GENERATE, FINISH_HABIT
    }
}