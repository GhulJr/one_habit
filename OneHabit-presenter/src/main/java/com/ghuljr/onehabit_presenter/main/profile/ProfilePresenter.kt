package com.ghuljr.onehabit_presenter.main.profile

import com.ghuljr.onehabit_presenter.base.BasePresenter
import com.ghuljr.onehabit_tools.di.FragmentScope
import io.reactivex.rxjava3.disposables.Disposable
import javax.inject.Inject

@FragmentScope
class ProfilePresenter @Inject constructor(): BasePresenter<ProfileView>() {

    override fun subscribeToView(view: ProfileView): Disposable {
        return Disposable.empty()
    }
}