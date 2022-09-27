package com.ghuljr.onehabit_presenter.main

import arrow.core.Nel
import com.ghuljr.onehabit_presenter.base.BaseView

interface MainView: BaseView<MainPresenter> {
    fun redirectToFillRemainingData()
    fun changeCurrentStep(currentStep: MainStep)
    fun setTitle(title: String)
    fun setSubtitle(subtitle: String)
    fun askForChoosingHabit()
    fun displayMilestoneSummary()
}