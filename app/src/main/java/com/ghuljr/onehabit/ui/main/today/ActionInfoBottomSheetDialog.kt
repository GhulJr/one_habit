package com.ghuljr.onehabit.ui.main.today

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ghuljr.onehabit.databinding.DialogActionInfoBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ActionInfoBottomSheetDialog : BottomSheetDialogFragment() {

    private var _viewBind: DialogActionInfoBinding? = null
    private val viewBind
        get() = _viewBind!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _viewBind = DialogActionInfoBinding.inflate(inflater, container, false)
        return viewBind.root
    }

    override fun onDestroyView() {
        _viewBind = null
        super.onDestroyView()
    }
}