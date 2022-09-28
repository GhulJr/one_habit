package com.ghuljr.onehabit.ui.create_habit

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.PopupMenu
import com.ghuljr.onehabit.R
import com.ghuljr.onehabit.databinding.ActivityCreateHabitBinding
import com.ghuljr.onehabit.ui.base.BaseActivity
import com.ghuljr.onehabit_presenter.create_habit.CreateHabitPresenter
import com.ghuljr.onehabit_presenter.create_habit.CreateHabitView

class CreateHabitActivity :
    BaseActivity<ActivityCreateHabitBinding, CreateHabitView, CreateHabitPresenter>(),
    CreateHabitView {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewBind.apply {
            val popupMenu = PopupMenu(this@CreateHabitActivity, habitAction).apply {
                inflate(R.menu.menu_habit_action)
                setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.eat -> true
                        R.id.not_eat -> true
                        R.id.train -> true
                        R.id.start_doing -> true
                        R.id.stop_doing -> true
                        else -> false
                    }
                }
            }
            habitAction.setOnClickListener { popupMenu.show() }
        }
    }

    override fun bindView(): ActivityCreateHabitBinding =
        ActivityCreateHabitBinding.inflate(layoutInflater)

    override fun getPresenterView(): CreateHabitView = this

    override fun handleCurrentStep(currentStep: CreateHabitPresenter.Step) {
        // TODO: handle visibility of incoming views
    }

    companion object {
        fun intent(from: Context) = Intent(from, CreateHabitActivity::class.java)
    }
}