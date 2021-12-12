package com.ghuljr.onehabit.ui.base

import androidx.annotation.CallSuper
import com.ghuljr.onehabit_presenter.base.BasePresenter
import com.ghuljr.onehabit_presenter.base.BaseView
import dagger.android.support.DaggerFragment
import javax.inject.Inject

abstract class BaseFragment<VIEW : BaseView<PRESENTER>, PRESENTER: BasePresenter<VIEW>>
    : DaggerFragment() {
    
    @Inject
    lateinit var presenter: PRESENTER

    @CallSuper
    override fun onStart() {
        super.onStart()
        presenter.attach(getPresenterView())
    }

    @CallSuper
    override fun onStop() {
        presenter.detach()
        super.onStop()
    }

    abstract fun getPresenterView(): VIEW
}