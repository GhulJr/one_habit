package com.ghuljr.onehabit.ui.create_habit

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.PopupMenu
import androidx.core.view.isVisible
import com.ghuljr.onehabit.R
import com.ghuljr.onehabit.databinding.ActivityCreateHabitBinding
import com.ghuljr.onehabit.ui.base.BaseActivity
import com.ghuljr.onehabit_presenter.create_habit.CreateHabitPresenter
import com.ghuljr.onehabit_presenter.create_habit.CreateHabitView
import com.ghuljr.onehabit_tools.model.HabitTopic

class CreateHabitActivity :
    BaseActivity<ActivityCreateHabitBinding, CreateHabitView, CreateHabitPresenter>(),
    CreateHabitView {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewBind.apply {
            toolbar.setNavigationOnClickListener { onBackPressed() }
            val popupMenu = PopupMenu(this@CreateHabitActivity, habitAction).apply {
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
            habitAction.setOnClickListener { popupMenu.show() }
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
        }
    }

    override fun setAction(habitTopic: HabitTopic) {
        viewBind.habitAction.setText(
            when(habitTopic) {
                HabitTopic.EAT -> R.string.eat
                HabitTopic.NOT_EAT -> R.string.not_eat
                HabitTopic.TRAIN -> R.string.train
                HabitTopic.START_DOING -> R.string.start_doing
                HabitTopic.STOP_DOING -> R.string.stop_doing
            }
        )
    }

    companion object {
        fun intent(from: Context) = Intent(from, CreateHabitActivity::class.java)
    }
}