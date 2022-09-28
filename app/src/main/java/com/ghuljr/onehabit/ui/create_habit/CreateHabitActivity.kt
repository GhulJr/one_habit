package com.ghuljr.onehabit.ui.create_habit

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ghuljr.onehabit.databinding.ActivityCreateHabitBinding
import com.ghuljr.onehabit.ui.base.BaseActivity
import com.ghuljr.onehabit_presenter.create_habit.CreateHabitPresenter
import com.ghuljr.onehabit_presenter.create_habit.CreateHabitView

class CreateHabitActivity : BaseActivity<ActivityCreateHabitBinding, CreateHabitView, CreateHabitPresenter>(), CreateHabitView {


    override fun bindView(): ActivityCreateHabitBinding = ActivityCreateHabitBinding.inflate(layoutInflater)
    override fun getPresenterView(): CreateHabitView = this


    companion object {
        fun intent(from: Context) = Intent(from, CreateHabitActivity::class.java)
    }
}