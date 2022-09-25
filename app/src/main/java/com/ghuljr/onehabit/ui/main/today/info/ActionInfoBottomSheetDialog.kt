package com.ghuljr.onehabit.ui.main.today.info

import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import com.ghuljr.onehabit.R
import com.ghuljr.onehabit.databinding.DialogActionInfoBinding
import com.ghuljr.onehabit.ui.main.today.list.generateTitle
import com.ghuljr.onehabit_presenter.main.today.ActionInfoItem
import com.ghuljr.onehabit_presenter.main.today.ActionType
import com.ghuljr.onehabit_presenter.main.today.info.ActionInfoPresenter
import com.ghuljr.onehabit_presenter.main.today.info.ActionInfoView
import com.ghuljr.onehabit_tools.extension.blankToOption
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlin.math.abs

// TODO: it might be problematic at some point, when state is restoring - better check
class ActionInfoBottomSheetDialog(
    override val presenter: ActionInfoPresenter,
    private val actionId: String
) : BottomSheetDialogFragment(), ActionInfoView {

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBind.apply {
            confirm.setOnClickListener { presenter.completeActionStep() }
            decline.setOnClickListener { presenter.revertCompleteActionStep() }
        }
    }

    @CallSuper
    override fun onStart() {
        super.onStart()
        presenter.attach(this)
        presenter.displayAction(actionId)
    }

    @CallSuper
    override fun onStop() {
        presenter.detach()
        super.onStop()
    }

    override fun onDestroyView() {
        _viewBind = null
        super.onDestroyView()
    }

    override fun displayActionInfo(item: ActionInfoItem) {
        viewBind.apply {
            toolbar.apply {
                title = item.habitTopic.generateTitle(resources, item.habitSubject, item.exceeded)
                subtitle =
                    if (item.quantity == null)
                        ""
                    else
                        when (item.type) {
                            ActionType.DAILY -> getString(R.string.today_quantity, item.quantity?.first.toString(), item.quantity?.second.toString())
                            ActionType.WEEKLY -> getString(
                                if (item.exceeded) R.string.today_quantity_exceeded
                                else R.string.today_quantity_remaining,
                                abs(item.quantity?.run { second - first } ?: 0).toString()
                            )
                        }
            }

            nextTimes.apply {
                val stringBuilder = SpannableStringBuilder()
                item.reminders?.forEachIndexed { index, time ->
                    if (index + 1 <= (item.quantity?.first ?: Int.MIN_VALUE))
                        stringBuilder.append("-$time-", StrikethroughSpan(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    else
                        stringBuilder.append("-$time-")
                    if (index + 1 < item.reminders!!.size)
                        stringBuilder.append("\n")
                }
                text = stringBuilder.ifBlank { getString(R.string.no_reminders) }
            }
        }
    }
}