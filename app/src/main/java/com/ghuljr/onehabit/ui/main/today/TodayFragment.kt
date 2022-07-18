package com.ghuljr.onehabit.ui.main.today

import android.view.LayoutInflater
import android.view.ViewGroup
import com.ghuljr.onehabit.databinding.FragmentTodayBinding
import com.ghuljr.onehabit.ui.base.BaseFragment
import com.ghuljr.onehabit_presenter.main.today.TodayPresenter
import com.ghuljr.onehabit_presenter.main.today.TodayView

class TodayFragment : BaseFragment<FragmentTodayBinding, TodayView, TodayPresenter>(), TodayView {

    override fun bindView(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTodayBinding = FragmentTodayBinding.inflate(layoutInflater, container, false)

    override fun getPresenterView(): TodayView = this
}