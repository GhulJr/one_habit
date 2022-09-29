package com.ghuljr.onehabit.ui.generate_milestone.generate

import android.view.LayoutInflater
import android.view.ViewGroup
import com.ghuljr.onehabit.databinding.FragmentAdjustIntensityBinding
import com.ghuljr.onehabit.ui.base.BaseFragment
import com.ghuljr.onehabit_presenter.generate_milestone.generate.AdjustIntensityPresenter
import com.ghuljr.onehabit_presenter.generate_milestone.generate.AdjustIntensityView

class AdjustIntensityFragment : BaseFragment<FragmentAdjustIntensityBinding, AdjustIntensityView, AdjustIntensityPresenter>(), AdjustIntensityView {

    override fun bindView(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAdjustIntensityBinding = FragmentAdjustIntensityBinding.inflate(layoutInflater, container, false)

    override fun getPresenterView(): AdjustIntensityView = this
}