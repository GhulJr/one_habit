package com.ghuljr.onehabit.ui.generate_milestone.generate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import arrow.core.Option
import com.ghuljr.onehabit.R
import com.ghuljr.onehabit.databinding.FragmentAdjustIntensityBinding
import com.ghuljr.onehabit.ui.base.BaseFragment
import com.ghuljr.onehabit_error.BaseEvent
import com.ghuljr.onehabit_error_android.event_handler.EventHandler
import com.ghuljr.onehabit_error_android.event_manager.SnackbarEventManager
import com.ghuljr.onehabit_presenter.generate_milestone.generate.AdjustIntensityPresenter
import com.ghuljr.onehabit_presenter.generate_milestone.generate.AdjustIntensityView
import com.google.android.material.snackbar.Snackbar

class AdjustIntensityFragment : BaseFragment<FragmentAdjustIntensityBinding, AdjustIntensityView, AdjustIntensityPresenter>(), AdjustIntensityView {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBind.apply {
            intensityFactorSlider.addOnChangeListener { _, value, _ -> presenter.setIntensity(value) }
        }
    }

    override fun bindView(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAdjustIntensityBinding = FragmentAdjustIntensityBinding.inflate(layoutInflater, container, false)

    override fun getPresenterView(): AdjustIntensityView = this

    override fun handleEvent(event: Option<BaseEvent>) {
        val eventManager = EventHandler(listOf(SnackbarEventManager(
            eventView = viewBind.root,
            duration = Snackbar.LENGTH_INDEFINITE,
            actionWithName = { } to getString(R.string.ok)
        )), viewBind.loadingIndicator)
        eventManager(event)
    }

    override fun setSliderScope(scope: Pair<Double, Double>) {
        viewBind.intensityFactorSlider.apply {
            valueFrom = scope.first.toFloat()
            valueTo = scope.second.toFloat()
        }
    }
}