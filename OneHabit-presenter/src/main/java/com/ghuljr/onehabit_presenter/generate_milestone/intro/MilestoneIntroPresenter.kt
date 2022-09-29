package com.ghuljr.onehabit_presenter.generate_milestone.intro

import com.ghuljr.onehabit_data.repository.UserMetadataRepository
import com.ghuljr.onehabit_presenter.base.BasePresenter
import com.ghuljr.onehabit_tools.di.ComputationScheduler
import com.ghuljr.onehabit_tools.di.FragmentScope
import com.ghuljr.onehabit_tools.di.UiScheduler
import com.ghuljr.onehabit_tools.extension.mapRight
import com.ghuljr.onehabit_tools.extension.onlyRight
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
    private val userMetadataRepository: UserMetadataRepository
) : BasePresenter<MilestoneIntroView>() {

    private val nextSubject = PublishSubject.create<Unit>()

    override fun subscribeToView(view: MilestoneIntroView): Disposable = CompositeDisposable(
        nextSubject
            .throttleFirst(500L, TimeUnit.MILLISECONDS, computationScheduler)
            .switchMapMaybe {
                userMetadataRepository.currentUser
                    .firstElement()
                    .mapRight { if(it.milestoneId == null) NextStep.GENERATE else NextStep.SUMMARY }
            }
            .onlyRight()
            .observeOn(uiScheduler)
            .subscribe { view.goNextStep(it) }

    )

    fun next() = nextSubject.onNext(Unit)

    enum class NextStep {
        SUMMARY, GENERATE
    }
}