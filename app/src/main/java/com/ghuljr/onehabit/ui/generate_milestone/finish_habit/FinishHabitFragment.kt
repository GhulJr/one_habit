package com.ghuljr.onehabit.ui.generate_milestone.finish_habit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ghuljr.onehabit.databinding.FragmentFinishHabitBinding
import com.ghuljr.onehabit.ui.base.BaseFragment
import com.ghuljr.onehabit_presenter.generate_milestone.finish_habit.FinishHabitPresenter
import com.ghuljr.onehabit_presenter.generate_milestone.finish_habit.FinishHabitView

class FinishHabitFragment : BaseFragment<FragmentFinishHabitBinding, FinishHabitView, FinishHabitPresenter>(), FinishHabitView {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBind.apply {
            activateTopTierHabit.setOnCheckedChangeListener { _, selected -> presenter.addHabitState(selected) }
            finishButton.setOnClickListener { presenter.finish() }
        }
    }

    override fun bindView(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFinishHabitBinding = FragmentFinishHabitBinding.inflate(layoutInflater, container, false)

    override fun getPresenterView(): FinishHabitView = this
}