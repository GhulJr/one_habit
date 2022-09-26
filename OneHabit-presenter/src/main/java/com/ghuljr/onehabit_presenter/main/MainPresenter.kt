package com.ghuljr.onehabit_presenter.main

import arrow.core.getOrElse
import com.ghuljr.onehabit_data.repository.LoggedInUserRepository
import com.ghuljr.onehabit_data.repository.UserMetadataRepository
import com.ghuljr.onehabit_presenter.base.BasePresenter
import com.ghuljr.onehabit_tools.di.ActivityScope
import com.ghuljr.onehabit_tools.di.UiScheduler
import com.ghuljr.onehabit_tools.extension.onlyDefined
import com.ghuljr.onehabit_tools.extension.onlyRight
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import javax.inject.Inject

@ActivityScope
class MainPresenter @Inject constructor(
    private val loggedInUserRepository: LoggedInUserRepository,
    private val userMetadataRepository: UserMetadataRepository,
    @UiScheduler private val uiScheduler: Scheduler
) : BasePresenter<MainView>() {

    private val currentStepSubject = BehaviorSubject.createDefault(MainStep.TODAY)

    override fun subscribeToView(view: MainView): Disposable = CompositeDisposable(
        loggedInUserRepository.userFlowable
            .onlyDefined()
            .filter { !it.isEmailVerified || it.username.isEmpty() }
            .observeOn(uiScheduler)
            .subscribe { view.redirectToFillRemainingData() },
        currentStepSubject
            .observeOn(uiScheduler)
            .withLatestFrom(loggedInUserRepository.userFlowable.toObservable().onlyDefined()) { currentStep, user -> currentStep to user }
            .subscribe { (currentStep, user) ->
                view.apply {
                    changeCurrentStep(currentStep)
                    if(currentStep == MainStep.PROFILE) {
                        setTitle(user.username.getOrElse { user.userId })
                        setSubtitle(user.email)
                    }
                }
            },
        userMetadataRepository.currentUser
            .onlyRight()
            .observeOn(uiScheduler)
            .subscribe {
                if(it.habitId == null)
                    view.askForChoosingHabit()
            }
    )

    fun setCurrentStep(currentStep: MainStep): Unit = currentStepSubject.onNext(currentStep)
}

enum class MainStep {
    TIMELINE, TODAY, PROFILE
}

