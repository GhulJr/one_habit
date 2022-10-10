package com.ghuljr.onehabit.ui.main.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import arrow.core.Option
import com.ghuljr.onehabit.R
import com.ghuljr.onehabit.databinding.FragmentProfileBinding
import com.ghuljr.onehabit.ui.base.BaseFragment
import com.ghuljr.onehabit.ui.create_habit.CreateHabitActivity
import com.ghuljr.onehabit.ui.main.MainActivity
import com.ghuljr.onehabit.ui.main.today.list.generateTitle
import com.ghuljr.onehabit.ui.habit_details.HabitDetailsActivity
import com.ghuljr.onehabit.ui.habits.HabitsActivity
import com.ghuljr.onehabit.ui.profile.email.ChangeEmailActivity
import com.ghuljr.onehabit.ui.profile.name.ChangeDisplayNameActivity
import com.ghuljr.onehabit.ui.profile.password.ChangePasswordActivity
import com.ghuljr.onehabit_error.BaseEvent
import com.ghuljr.onehabit_error_android.event_handler.EventHandler
import com.ghuljr.onehabit_error_android.event_manager.SnackbarEventManager
import com.ghuljr.onehabit_presenter.main.MainStep
import com.ghuljr.onehabit_presenter.main.profile.ProfilePresenter
import com.ghuljr.onehabit_presenter.main.profile.ProfileView
import com.ghuljr.onehabit_tools.model.HabitTopic
import com.google.android.material.snackbar.Snackbar

class ProfileFragment : BaseFragment<FragmentProfileBinding, ProfileView, ProfilePresenter>(), ProfileView {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as? MainActivity)?.setCurrentStep(MainStep.PROFILE)

        viewBind.apply {
            addHabit.setOnClickListener { startActivity(CreateHabitActivity.intent(requireContext())) }
            currentHabitDetailsButton.setOnClickListener { presenter.openCurrentHabitDetails() }
            allHabits.setOnClickListener { startActivity(HabitsActivity.intent(requireContext())) }
            changeName.setOnClickListener { startActivity(ChangeDisplayNameActivity.intent(requireContext())) }
            changeEmail.setOnClickListener { startActivity(ChangeEmailActivity.intent(requireContext())) }
            changePassword.setOnClickListener { startActivity(ChangePasswordActivity.intent(requireContext())) }
        }
    }

    override fun bindView(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentProfileBinding = FragmentProfileBinding.inflate(layoutInflater, container, false)

    override fun getPresenterView(): ProfileView = this

    override fun displayCurrentHabitData(habitTopic: HabitTopic, habitSubject: String, intensityProgress: Int) {
        viewBind.apply {
            currentHabitTitle.text = habitTopic.generateTitle(resources, habitSubject)
            currentHabitPercentageProgress.text = "$intensityProgress%"
            currentHabitProgressBar.progress = intensityProgress
        }
    }

    override fun openCurrentHabitDetails(habitId: String) {
        startActivity(HabitDetailsActivity.intent(requireContext(), habitId))
    }

    override fun handleEvent(event: Option<BaseEvent>) {
        val eventHandler = EventHandler(listOf(SnackbarEventManager(
            eventView = viewBind.root,
            duration = Snackbar.LENGTH_INDEFINITE,
            actionWithName = { } to getString(R.string.ok)
        )), viewBind.loadingIndicator)

        eventHandler(event)
    }
}