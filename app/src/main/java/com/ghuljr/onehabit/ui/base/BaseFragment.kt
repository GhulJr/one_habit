package com.ghuljr.onehabit.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.viewbinding.ViewBinding
import com.ghuljr.onehabit_presenter.base.BasePresenter
import com.ghuljr.onehabit_presenter.base.BaseView
import dagger.android.support.DaggerFragment
import javax.inject.Inject

abstract class BaseFragment<BINDING: ViewBinding, VIEW : BaseView<PRESENTER>, PRESENTER: BasePresenter<VIEW>>
    : DaggerFragment() {
    
    @Inject
    lateinit var presenter: PRESENTER
    protected var viewBind: BINDING? = null

    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBind = bindView(inflater, container)
        return viewBind!!.root
    }

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

    @CallSuper
    override fun onDestroyView() {
        viewBind = null
        super.onDestroyView()
    }

    abstract fun getPresenterView(): VIEW
    abstract fun bindView(layoutInflater: LayoutInflater, container: ViewGroup?): BINDING
}