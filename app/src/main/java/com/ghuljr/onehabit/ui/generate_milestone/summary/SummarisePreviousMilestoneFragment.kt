package com.ghuljr.onehabit.ui.generate_milestone.summary

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.ghuljr.onehabit.R
import com.ghuljr.onehabit.databinding.FragmentSummarisePreviousMilestoneBinding
import com.ghuljr.onehabit.ui.base.BaseFragment
import com.ghuljr.onehabit_presenter.generate_milestone.summary.SummarisePreviousMilestonePresenter
import com.ghuljr.onehabit_presenter.generate_milestone.summary.SummarisePreviousMilestoneView

class SummarisePreviousMilestoneFragment : BaseFragment<FragmentSummarisePreviousMilestoneBinding, SummarisePreviousMilestoneView, SummarisePreviousMilestonePresenter>(), SummarisePreviousMilestoneView {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBind.apply {
            nextButton.setOnClickListener { presenter.goNext() }
        }
      //  view.findViewById<Button>(R.id.next_button).setOnClickListener { findNavController().navigate(SummarisePreviousMilestoneFragmentDirections.toGenerate()) }
    }

    override fun bindView(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSummarisePreviousMilestoneBinding = FragmentSummarisePreviousMilestoneBinding.inflate(layoutInflater, container, false)

    override fun getPresenterView(): SummarisePreviousMilestoneView = this

}