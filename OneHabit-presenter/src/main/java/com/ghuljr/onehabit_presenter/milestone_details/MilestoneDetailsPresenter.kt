package com.ghuljr.onehabit_presenter.milestone_details

import com.ghuljr.onehabit_data.repository.GoalRepository
import com.ghuljr.onehabit_data.repository.MilestoneRepository
import com.ghuljr.onehabit_presenter.base.BasePresenter
import com.ghuljr.onehabit_tools.di.ActivityScope
import com.ghuljr.onehabit_tools.di.UiScheduler
import com.ghuljr.onehabit_tools.extension.mapRight
import com.ghuljr.onehabit_tools.extension.onlyRight
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import javax.inject.Inject

@ActivityScope
class MilestoneDetailsPresenter @Inject constructor(): BasePresenter<MilestoneDetailsView>() {

    override fun subscribeToView(view: MilestoneDetailsView): Disposable = CompositeDisposable()

}