package com.ghuljr.onehabit.ui.generate_milestone.finish_habit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import arrow.core.Option
import com.ghuljr.onehabit.R
import com.ghuljr.onehabit.databinding.FragmentFinishHabitBinding
import com.ghuljr.onehabit.ui.base.BaseFragment
import com.ghuljr.onehabit_error.BaseEvent
import com.ghuljr.onehabit_error_android.event_handler.EventHandler
import com.ghuljr.onehabit_error_android.event_manager.SnackbarEventManager
import com.ghuljr.onehabit_presenter.generate_milestone.finish_habit.FinishHabitPresenter
import com.ghuljr.onehabit_presenter.generate_milestone.finish_habit.FinishHabitView
import com.google.android.material.snackbar.Snackbar

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

    override fun handleEvent(event: Option<BaseEvent>) {
        val eventManager = EventHandler(listOf(SnackbarEventManager(
            eventView = viewBind.root,
            duration = Snackbar.LENGTH_INDEFINITE,
            actionWithName = { } to getString(R.string.ok)
        )), viewBind.loadingIndicator)

        eventManager(event)
    }

    override fun finish() {
        requireActivity().finish()
    }
}