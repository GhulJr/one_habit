package com.ghuljr.onehabit_presenter.generate_milestone.generate

import arrow.core.Option
import com.ghuljr.onehabit_error.BaseEvent
import com.ghuljr.onehabit_presenter.base.BaseView

interface AdjustIntensityView : BaseView<AdjustIntensityPresenter> {

    fun handleEvent(event: Option<BaseEvent>)
    fun setSliderScope(scope: Pair<Double, Double>)
}