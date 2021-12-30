package com.ghuljr.onehabit.ui.intro.change_data.verify_email

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.ghuljr.onehabit.databinding.FragmentVerifyEmailFinishedBinding
import com.ghuljr.onehabit.ui.base.BaseFragment
import com.ghuljr.onehabit_presenter.intro.fill_data.verify_email.VerifyEmailFinishedPresenter
import com.ghuljr.onehabit_presenter.intro.fill_data.verify_email.VerifyEmailFinishedView
import io.reactivex.rxjava3.core.Observable

class VerifyEmailFinishedFragment : BaseFragment<FragmentVerifyEmailFinishedBinding, VerifyEmailFinishedView, VerifyEmailFinishedPresenter>(), VerifyEmailFinishedView {

    private var navController: NavController? = null

    override fun setUpView(viewBind: FragmentVerifyEmailFinishedBinding) {
        navController = findNavController()
    }

    override fun destroyView() {
        navController = null
    }

    override fun bindView(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentVerifyEmailFinishedBinding = FragmentVerifyEmailFinishedBinding.inflate(layoutInflater, container, false)

    override fun getPresenterView(): VerifyEmailFinishedView = this

    override fun nextClickObservable(): Observable<Unit> = viewBind!!.nextButton.throttleClicks()

    override fun goNext() {
       navController?.navigate(VerifyEmailFinishedFragmentDirections.actionVerifyEmailFinishedFragmentToUsernameFragment())
    }
}