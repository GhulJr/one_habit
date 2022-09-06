package com.ghuljr.onehabit.ui.main.profile

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import com.ghuljr.onehabit.R
import com.ghuljr.onehabit.databinding.FragmentProfileBinding
import com.ghuljr.onehabit.ui.base.BaseFragment
import com.ghuljr.onehabit.ui.main.MainActivity
import com.ghuljr.onehabit_presenter.main.MainStep
import com.ghuljr.onehabit_presenter.main.profile.ProfilePresenter
import com.ghuljr.onehabit_presenter.main.profile.ProfileView

class ProfileFragment : BaseFragment<FragmentProfileBinding, ProfileView, ProfilePresenter>(), ProfileView {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as? MainActivity)?.setCurrentStep(MainStep.PROFILE)
    }

    override fun bindView(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentProfileBinding = FragmentProfileBinding.inflate(layoutInflater, container, false)

    override fun getPresenterView(): ProfileView = this
}