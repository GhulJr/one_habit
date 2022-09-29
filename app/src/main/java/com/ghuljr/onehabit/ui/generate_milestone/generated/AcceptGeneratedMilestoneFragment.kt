package com.ghuljr.onehabit.ui.generate_milestone.generated

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.ghuljr.onehabit.R
import com.ghuljr.onehabit.ui.generate_milestone.summary.SummarisePreviousMilestoneFragmentDirections

class AcceptGeneratedMilestoneFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_accept_generated_milestone, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<Button>(R.id.finish_button).setOnClickListener { requireActivity().finish() }
    }
}