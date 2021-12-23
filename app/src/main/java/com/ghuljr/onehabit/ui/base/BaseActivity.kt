package com.ghuljr.onehabit.ui.base

import android.os.Bundle
import android.os.PersistableBundle
import androidx.annotation.CallSuper
import androidx.viewbinding.ViewBinding
import com.ghuljr.onehabit_presenter.base.BasePresenter
import com.ghuljr.onehabit_presenter.base.BaseView
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

abstract class BaseActivity<BINDING: ViewBinding, VIEW : BaseView<PRESENTER>, PRESENTER: BasePresenter<VIEW>>
    : DaggerAppCompatActivity() {

    @Inject
    lateinit var presenter: PRESENTER
    protected val viewBind by lazy { bindView() }

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBind.root)
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

    abstract fun getPresenterView(): VIEW
    abstract fun bindView(): BINDING
}