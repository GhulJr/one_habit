package com.ghuljr.onehabit_presenter.intro.fill_data.verify_email

import com.ghuljr.onehabit_data.repository.LoggedInUserRepository
import com.ghuljr.onehabit_presenter.base.BasePresenter
import com.ghuljr.onehabit_tools.di.ComputationScheduler
import com.ghuljr.onehabit_tools.di.FragmentScope
import com.ghuljr.onehabit_tools.di.UiScheduler
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.Disposable
import javax.inject.Inject

@FragmentScope
class VerifyEmailPresenter @Inject constructor(
    private val repository: LoggedInUserRepository,
    @ComputationScheduler private val computationScheduler: Scheduler,
    @UiScheduler private val uiScheduler: Scheduler
): BasePresenter<VerifyEmailView>() {
    override fun subscribeToView(view: VerifyEmailView): Disposable = Disposable.empty()
}