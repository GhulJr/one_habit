package com.ghuljr.onehabit_presenter.main.profile

import com.ghuljr.onehabit_data.repository.HabitRepository
import com.ghuljr.onehabit_data.repository.LoggedInUserRepository
import com.ghuljr.onehabit_data.repository.MilestoneRepository
import com.ghuljr.onehabit_presenter.base.BasePresenter
import com.ghuljr.onehabit_tools.di.FragmentScope
import com.ghuljr.onehabit_tools.di.UiScheduler
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.Disposable
import javax.inject.Inject

// TODO: handle removing habit with all dependencies in withing the app
@FragmentScope
class ProfilePresenter @Inject constructor(
    @UiScheduler private val uiScheduler: Scheduler,
    private val userRepository: LoggedInUserRepository,
    private val habitRepository: HabitRepository,
    private val milestoneRepository: MilestoneRepository
): BasePresenter<ProfileView>() {

    override fun subscribeToView(view: ProfileView): Disposable {
        return Disposable.empty()
    }
}