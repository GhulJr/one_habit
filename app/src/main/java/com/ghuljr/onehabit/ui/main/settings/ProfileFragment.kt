package com.ghuljr.onehabit.ui.main.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ghuljr.onehabit.R
import com.ghuljr.onehabit.ui.main.MainActivity
import com.ghuljr.onehabit_presenter.main.MainStep

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as? MainActivity)?.setCurrentStep(MainStep.PROFILE)
    }
}