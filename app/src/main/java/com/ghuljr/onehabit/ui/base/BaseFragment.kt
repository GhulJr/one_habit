package com.ghuljr.onehabit.ui.base

import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import com.ghuljr.onehabit_presenter.base.BasePresenter
import com.ghuljr.onehabit_presenter.base.BaseView
import com.ghuljr.onehabit_presenter.base.InitialValuesHolder
import org.koin.android.scope.AndroidScopeComponent
import org.koin.androidx.scope.fragmentScope
import org.koin.core.scope.Scope

abstract class BaseFragment<VIEW : BaseView<PRESENTER, VALUES>, PRESENTER: BasePresenter<VIEW, VALUES>, VALUES: InitialValuesHolder>
    : Fragment(), AndroidScopeComponent  {

    override val scope: Scope by fragmentScope()

    @CallSuper
    override fun onStart() {
        super.onStart()
        getPresenter().attach(getBaseView(), getInitialValues())
    }

    @CallSuper
    override fun onStop() {
        getPresenter().detach()
        super.onStop()
    }

    abstract fun getBaseView(): VIEW
    abstract fun getPresenter(): PRESENTER
    abstract fun getInitialValues(): VALUES
}