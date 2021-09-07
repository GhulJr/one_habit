package com.ghuljr.onehabit.ui.base

import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import com.ghuljr.onehabit_presenter.base.BasePresenter
import com.ghuljr.onehabit_presenter.base.BaseView
import com.ghuljr.onehabit_presenter.base.InitialValuesHolder
import org.koin.android.scope.AndroidScopeComponent
import org.koin.androidx.scope.activityRetainedScope
import org.koin.core.scope.Scope

abstract class BaseActivity<VIEW : BaseView<PRESENTER>, PRESENTER: BasePresenter<VIEW, VALUES>, VALUES: InitialValuesHolder>
    : AppCompatActivity(), AndroidScopeComponent {

    override val scope: Scope by activityRetainedScope()

    @CallSuper
    override fun onStart() {
        super.onStart()
        getPresenter().attach(getView(), getInitialValues())
    }

    @CallSuper
    override fun onStop() {
        getPresenter().detach()
        super.onStop()
    }



    abstract fun getView(): VIEW
    abstract fun getPresenter(): PRESENTER
    abstract fun getInitialValues(): VALUES

}