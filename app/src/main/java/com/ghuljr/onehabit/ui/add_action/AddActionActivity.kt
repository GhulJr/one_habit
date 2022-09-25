package com.ghuljr.onehabit.ui.add_action

import android.content.Context
import android.content.Intent
import com.ghuljr.onehabit.databinding.ActivityAddActionBinding
import android.os.Bundle
import com.ghuljr.onehabit.ui.base.BaseActivity
import com.ghuljr.onehabit_presenter.add_action.AddActionPresenter
import com.ghuljr.onehabit_presenter.add_action.AddActionView

class AddActionActivity : BaseActivity<ActivityAddActionBinding, AddActionView, AddActionPresenter>(), AddActionView {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun bindView(): ActivityAddActionBinding = ActivityAddActionBinding.inflate(layoutInflater)
    override fun getPresenterView(): AddActionView = this


    companion object {
        fun intent(from: Context) = Intent(from, AddActionActivity::class.java)
    }

}