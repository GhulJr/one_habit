package com.ghuljr.onehabit.ui.main

import android.os.Bundle
import com.ghuljr.onehabit.R
import com.ghuljr.onehabit.databinding.ActivityMainBinding
import com.ghuljr.onehabit.ui.base.BaseActivity
import com.ghuljr.onehabit_presenter.base.BaseView
import com.ghuljr.onehabit_presenter.main.MainPresenter
import com.ghuljr.onehabit_presenter.main.MainView

class MainActivity : BaseActivity<ActivityMainBinding, MainView, MainPresenter>(), MainView {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBind.root)
    }

    override fun getPresenterView(): MainView = this
    override fun bindView(): ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)
}