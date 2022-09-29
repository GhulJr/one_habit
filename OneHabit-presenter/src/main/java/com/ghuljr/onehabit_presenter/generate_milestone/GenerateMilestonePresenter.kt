package com.ghuljr.onehabit_presenter.generate_milestone

import com.ghuljr.onehabit_data.repository.MilestoneRepository
import com.ghuljr.onehabit_data.repository.UserMetadataRepository
import com.ghuljr.onehabit_presenter.base.BasePresenter
import com.ghuljr.onehabit_tools.di.ActivityScope
import com.ghuljr.onehabit_tools.di.ComputationScheduler
import com.ghuljr.onehabit_tools.di.UiScheduler
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import javax.inject.Inject

@ActivityScope
class GenerateMilestonePresenter @Inject constructor(
    @ComputationScheduler private val computationScheduler: Scheduler,
    @UiScheduler private val uiScheduler: Scheduler,
    private val userMetadataRepository: UserMetadataRepository,
    private val milestoneRepository: MilestoneRepository
) : BasePresenter<GenerateMilestoneView>() {

    override fun subscribeToView(view: GenerateMilestoneView): Disposable = CompositeDisposable(

    )
}