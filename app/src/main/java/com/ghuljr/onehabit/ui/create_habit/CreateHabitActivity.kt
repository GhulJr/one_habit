package com.ghuljr.onehabit.ui.create_habit

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import arrow.core.Option
import com.ghuljr.onehabit.R
import com.ghuljr.onehabit.databinding.ActivityCreateHabitBinding
import com.ghuljr.onehabit.ui.base.BaseActivity
import com.ghuljr.onehabit_error.BaseEvent
import com.ghuljr.onehabit_error_android.event_handler.EventHandler
import com.ghuljr.onehabit_error_android.event_manager.SnackbarEventManager
import com.ghuljr.onehabit_presenter.create_habit.CreateHabitPresenter
import com.ghuljr.onehabit_presenter.create_habit.CreateHabitView
import com.ghuljr.onehabit_tools.model.HabitTopic
import com.google.android.material.snackbar.Snackbar

class CreateHabitActivity :
    BaseActivity<ActivityCreateHabitBinding, CreateHabitView, CreateHabitPresenter>(),
    CreateHabitView {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewBind.apply {
            toolbar.setNavigationOnClickListener { onBackPressed() }
            val actionMenu = PopupMenu(this@CreateHabitActivity, habitAction).apply {
                inflate(R.menu.menu_habit_action)
                setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.eat -> presenter.actionEat()
                        R.id.not_eat -> presenter.actionNotEat()
                        R.id.train -> presenter.actionTrain()
                        R.id.start_doing -> presenter.actionStartDoing()
                        R.id.stop_doing -> presenter.actionStopDoing()
                        else -> return@setOnMenuItemClickListener false
                    }
                    return@setOnMenuItemClickListener true
                }
            }
            val frequencyMenu = PopupMenu(this@CreateHabitActivity, frequency).apply {
                inflate(R.menu.menu_habit_frequency)
                setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.daily -> presenter.setDaily()
                        R.id.weekly -> presenter.setWeekly()
                        else -> return@setOnMenuItemClickListener false
                    }
                    return@setOnMenuItemClickListener true
                }
            }
            habitAction.setOnClickListener { actionMenu.show() }
            habitSubject.doOnTextChanged { text, _, _, _ -> text?.let { presenter.setSubject(it.toString()) } }
            baseIntensity.doOnTextChanged { text, _, _, _ -> text?.let { presenter.setBaseIntensity(it.toString().toIntOrNull() ?: 0) } }
            frequency.setOnClickListener { frequencyMenu.show() }
            desiredIntensity.doOnTextChanged { text, _, _, _ -> text?.let { presenter.setDesiredIntensity(it.toString().toIntOrNull() ?: 0) } }
            intensityFactorSlider.addOnChangeListener { _, value, _ -> presenter.intensityFactor(value) }
            makeActiveCheckbox.setOnCheckedChangeListener { _, selected -> presenter.setAsActive(selected) }
            createHabit.setOnClickListener { presenter.createHabit() }
        }
    }

    override fun bindView(): ActivityCreateHabitBinding =
        ActivityCreateHabitBinding.inflate(layoutInflater)

    override fun getPresenterView(): CreateHabitView = this

    override fun handleCurrentStep(activeSteps: Set<CreateHabitPresenter.Step>) {
        viewBind.apply {
            habitActionContainer.isVisible = activeSteps.contains(CreateHabitPresenter.Step.ACTIVITY)
            habitSubjectContainer.isVisible = activeSteps.contains(CreateHabitPresenter.Step.SUBJECT)
            intensityHeader.isVisible = activeSteps.contains(CreateHabitPresenter.Step.INTENSITY_BASE)
            intensityBaseContainer.isVisible = intensityHeader.isVisible
            intensityDesiredContainer.isVisible = activeSteps.contains(CreateHabitPresenter.Step.INTENSITY_DESIRED)
            intensityFactorHeader.isVisible = activeSteps.contains(CreateHabitPresenter.Step.INTENSITY_FACTOR)
            intensityFactorSlider.isVisible = intensityFactorHeader.isVisible
            finishHeader.isVisible = activeSteps.contains(CreateHabitPresenter.Step.ALLOW_CREATE)
            makeActiveCheckbox.isVisible = finishHeader.isVisible
            createHabit.isVisible = finishHeader.isVisible

            scrollView.postDelayed({ scrollView.fullScroll(View.FOCUS_DOWN) }, 500L)
        }
    }

    override fun setAction(habitTopic: HabitTopic) {
        viewBind.habitAction.setText(
            when (habitTopic) {
                HabitTopic.EAT -> R.string.eat
                HabitTopic.NOT_EAT -> R.string.not_eat
                HabitTopic.TRAIN -> R.string.train
                HabitTopic.START_DOING -> R.string.start_doing
                HabitTopic.STOP_DOING -> R.string.stop_doing
            }
        )
    }

    override fun setHabitFrequency(frequency: Int) {
        viewBind.frequency.setText(
            if (frequency == 0) R.string.weekly
            else R.string.daily
        )
    }

    override fun handleEvent(event: Option<BaseEvent>) {
        val eventHandler = EventHandler(listOf(SnackbarEventManager(
            eventView = viewBind.root,
            duration = Snackbar.LENGTH_INDEFINITE,
            actionWithName = { } to getString(R.string.ok)
        )), viewBind.loadingIndicator)
        eventHandler(event)
    }

    override fun close() {
        onBackPressed()
    }

    companion object {
        fun intent(from: Context) = Intent(from, CreateHabitActivity::class.java)
    }
}