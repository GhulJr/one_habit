package com.ghuljr.onehabit_presenter.main

import arrow.core.Nel
import com.ghuljr.onehabit_data.repository.LoggedInUserRepository
import com.ghuljr.onehabit_presenter.base.BasePresenter
import com.ghuljr.onehabit_tools.di.ActivityScope
import com.ghuljr.onehabit_tools.extension.onlyDefined
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import javax.inject.Inject

@ActivityScope
class MainPresenter @Inject constructor(
    private val loggedInUserRepository: LoggedInUserRepository
) : BasePresenter<MainView>() {

    override fun subscribeToView(view: MainView): Disposable = CompositeDisposable(
        loggedInUserRepository.userFlowable
            .onlyDefined()
            .map {
                mutableListOf<FillRemainingDataSteps>().apply {
                    if(!it.isEmailVerified) add(FillRemainingDataSteps.VERIFY_EMAIL)
                    if(it.username.isEmpty()) add(FillRemainingDataSteps.SET_DISPLAY_NAME)
                }
            }
            .map { Nel.fromList(it) }
            .filter { it.isDefined() }
            .map { it.orNull()!! }
            .subscribe { view.redirectToFillRemainingData(it) }
    )
}

