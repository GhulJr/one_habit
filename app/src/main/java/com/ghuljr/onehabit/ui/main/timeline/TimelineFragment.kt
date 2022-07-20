package com.ghuljr.onehabit.ui.main.timeline

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.ghuljr.onehabit.R
import com.ghuljr.onehabit.ui.main.MainActivity
import com.ghuljr.onehabit_presenter.main.MainStep


class TimelineFragment : Fragment(R.layout.fragment_timeline) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as? MainActivity)?.setCurrentStep(MainStep.TIMELINE)
    }
}