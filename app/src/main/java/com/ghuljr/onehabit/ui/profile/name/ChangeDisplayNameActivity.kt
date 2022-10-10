package com.ghuljr.onehabit.ui.profile.name
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.widget.doOnTextChanged
import arrow.core.Option
import com.ghuljr.onehabit.R
import com.ghuljr.onehabit.databinding.ActivityChangeDisplayNameBinding
import com.ghuljr.onehabit.ui.base.BaseActivity
import com.ghuljr.onehabit_error.BaseEvent
import com.ghuljr.onehabit_error_android.event_handler.EventHandler
import com.ghuljr.onehabit_error_android.event_manager.SnackbarEventManager
import com.ghuljr.onehabit_presenter.profile.name.ChangeDisplayNamePresenter
import com.ghuljr.onehabit_presenter.profile.name.ChangeDisplayNameView
import com.google.android.material.snackbar.Snackbar

class ChangeDisplayNameActivity : BaseActivity<ActivityChangeDisplayNameBinding, ChangeDisplayNameView, ChangeDisplayNamePresenter>(), ChangeDisplayNameView {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBind.apply {
            sendButton.setOnClickListener { presenter.setNewName() }
            toolbar.setNavigationOnClickListener { onBackPressed() }
            nameInput.doOnTextChanged { text, _, _, _ -> text?.toString()?.let { presenter.nameChanged(it) } }
        }
    }

    override fun bindView(): ActivityChangeDisplayNameBinding = ActivityChangeDisplayNameBinding.inflate(layoutInflater)

    override fun getPresenterView(): ChangeDisplayNameView = this

    override fun close() {
        onBackPressed()
    }

    override fun handleEvent(event: Option<BaseEvent>) {
        val eventManager = EventHandler(listOf(SnackbarEventManager(
            eventView = viewBind.root,
            duration = Snackbar.LENGTH_INDEFINITE,
            actionWithName = { } to getString(R.string.ok)
        )), viewBind.loadingIndicator)
        eventManager(event)
    }

    companion object {

        fun intent(from: Context) = Intent(from, ChangeDisplayNameActivity::class.java)
    }
}