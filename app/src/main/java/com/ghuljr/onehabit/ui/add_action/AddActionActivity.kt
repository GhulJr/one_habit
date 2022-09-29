package com.ghuljr.onehabit.ui.add_action

import android.content.Context
import android.content.Intent
import com.ghuljr.onehabit.databinding.ActivityAddActionBinding
import android.os.Bundle
import androidx.core.view.isVisible
import arrow.core.Option
import arrow.core.toOption
import com.ghuljr.onehabit.R
import com.ghuljr.onehabit.ui.base.BaseActivity
import com.ghuljr.onehabit_error.BaseEvent
import com.ghuljr.onehabit_error_android.event_handler.EventHandler
import com.ghuljr.onehabit_error_android.event_manager.SnackbarEventManager
import com.ghuljr.onehabit_presenter.add_action.AddActionPresenter
import com.ghuljr.onehabit_presenter.add_action.AddActionView
import com.ghuljr.onehabit_tools_android.extension.debouncedTextChanges
import com.google.android.material.snackbar.Snackbar
import io.reactivex.rxjava3.core.Observable

class AddActionActivity :
    BaseActivity<ActivityAddActionBinding, AddActionView, AddActionPresenter>(), AddActionView {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.init(intent.getStringExtra(EXTRA_GOAL_ID)!! to intent.getStringExtra(EXTRA_ACTION_ID).toOption())
        viewBind.apply {
            addActionButton.setOnClickListener { presenter.createAction() }
        }
    }

    override fun bindView(): ActivityAddActionBinding =
        ActivityAddActionBinding.inflate(layoutInflater)

    override fun getPresenterView(): AddActionView = this

    override fun actionNameChangedObservable(): Observable<String> =
        viewBind.actionNameInput.debouncedTextChanges().startWithItem("")

    override fun handleEvent(event: Option<BaseEvent>) {
        val eventHandler = EventHandler(listOf(SnackbarEventManager(
            eventView = viewBind.root,
            duration = Snackbar.LENGTH_INDEFINITE,
            actionWithName = { } to getString(R.string.ok)
        )), viewBind.loadingIndicator)

        eventHandler(event)
    }

    override fun close() {
        finish()
    }

    override fun setActionTitle(title: String) {
        viewBind.actionNameInput.setText(title)
    }

    override fun enableSetTitle(enable: Boolean) {
        viewBind.apply {
            titleHeader.isVisible = enable
            actionNameInput.isVisible = enable
        }
    }

    companion object {
        private const val EXTRA_GOAL_ID = "extra_goal_id"
        private const val EXTRA_ACTION_ID = "extra_action_id"

        fun intent(from: Context, goalId: String, actionId: String? = null) =
            Intent(from, AddActionActivity::class.java).apply {
                putExtra(EXTRA_GOAL_ID, goalId)
                putExtra(EXTRA_ACTION_ID, actionId)
            }
    }
}