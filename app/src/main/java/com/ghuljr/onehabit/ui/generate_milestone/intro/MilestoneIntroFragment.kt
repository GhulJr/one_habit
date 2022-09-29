package com.ghuljr.onehabit.ui.generate_milestone.intro

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.ghuljr.onehabit.R
import com.ghuljr.onehabit.databinding.FragmentMilestoneIntroBinding
import com.ghuljr.onehabit.databinding.FragmentTodayBinding
import com.ghuljr.onehabit.ui.base.BaseFragment
import com.ghuljr.onehabit_presenter.generate_milestone.intro.MilestoneIntroPresenter
import com.ghuljr.onehabit_presenter.generate_milestone.intro.MilestoneIntroView

class MilestoneIntroFragment :
    BaseFragment<FragmentMilestoneIntroBinding, MilestoneIntroView, MilestoneIntroPresenter>(),
    MilestoneIntroView {

    override fun bindView(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMilestoneIntroBinding =
        FragmentMilestoneIntroBinding.inflate(layoutInflater, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBind.nextButton.setOnClickListener { presenter.next() }
    }

    override fun getPresenterView(): MilestoneIntroView = this

    override fun goNextStep(nextStep: MilestoneIntroPresenter.NextStep) {
        findNavController()
            .navigate(
                when (nextStep) {
                    MilestoneIntroPresenter.NextStep.SUMMARY -> MilestoneIntroFragmentDirections.toSummary()
                    MilestoneIntroPresenter.NextStep.GENERATE -> MilestoneIntroFragmentDirections.toGenerate()
                }
            )
    }

}