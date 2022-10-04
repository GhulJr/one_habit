package com.ghuljr.onehabit.ui.main.today.info

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.core.view.isVisible
import com.ghuljr.onehabit.R
import com.ghuljr.onehabit.databinding.DialogActionInfoBinding
import com.ghuljr.onehabit.ui.add_action.AddActionActivity
import com.ghuljr.onehabit.ui.main.today.list.generateTitleWithOpposition
import com.ghuljr.onehabit_presenter.main.today.ActionInfoItem
import com.ghuljr.onehabit_presenter.main.today.ActionType
import com.ghuljr.onehabit_presenter.main.today.info.ActionInfoPresenter
import com.ghuljr.onehabit_presenter.main.today.info.ActionInfoView
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

            toolbar.setOnMenuItemClickListener {
                when(it.itemId) {
                    R.id.edit -> presenter.editAction()
                    R.id.delete -> presenter.removeAction()
                    else -> return@setOnMenuItemClickListener false
                }
                return@setOnMenuItemClickListener true
            }
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
                title = item.customTitle ?: item.habitTopic.generateTitleWithOpposition(resources, item.habitSubject, item.exceeded)
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
            confirm.isVisible = item.confirmAvailable
            decline.isVisible = item.declineAvailable
            toolbar.menu.findItem(R.id.delete).isVisible = item.editable
        }
    }

    override fun close() {
        dismiss()
    }

    override fun editAction(goalId: String, actionId: String) {
        startActivity(AddActionActivity.intent(requireContext(), goalId, actionId))
    }
}