package com.ghuljr.onehabit_presenter.generate_milestone.intro

import arrow.core.Option
import com.ghuljr.onehabit_error.BaseEvent
import com.ghuljr.onehabit_presenter.base.BaseView

interface MilestoneIntroView : BaseView<MilestoneIntroPresenter> {

    fun goNextStep(nextStep: MilestoneIntroPresenter.NextStep)
    fun handleEvent(event: Option<BaseEvent>)
}