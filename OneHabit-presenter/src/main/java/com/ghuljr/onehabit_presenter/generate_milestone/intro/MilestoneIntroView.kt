package com.ghuljr.onehabit_presenter.generate_milestone.intro

import com.ghuljr.onehabit_presenter.base.BaseView

interface MilestoneIntroView : BaseView<MilestoneIntroPresenter> {

    fun goNextStep(nextStep: MilestoneIntroPresenter.NextStep)
}