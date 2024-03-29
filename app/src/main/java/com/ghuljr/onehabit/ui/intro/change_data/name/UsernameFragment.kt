package com.ghuljr.onehabit.ui.intro.change_data.name

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import arrow.core.Option
import com.ghuljr.onehabit.R
import com.ghuljr.onehabit.databinding.FragmentUsernameBinding
import com.ghuljr.onehabit.ui.base.BaseFragment
import com.ghuljr.onehabit.ui.main.MainActivity
import com.ghuljr.onehabit_error.BaseEvent
import com.ghuljr.onehabit_error.LoadingEvent
import com.ghuljr.onehabit_error.ValidationError
import com.ghuljr.onehabit_error_android.event_handler.EventHandler
import com.ghuljr.onehabit_error_android.event_manager.SnackbarEventManager
import com.ghuljr.onehabit_error_android.extension.setErrorOption
import com.ghuljr.onehabit_presenter.intro.fill_data.name.UsernamePresenter
import com.ghuljr.onehabit_presenter.intro.fill_data.name.UsernameView
import com.ghuljr.onehabit_tools_android.extension.debouncedTextChanges
import com.ghuljr.onehabit_tools_android.extension.throttleClicks
import com.google.android.material.snackbar.Snackbar
import io.reactivex.rxjava3.core.Observable

class UsernameFragment : BaseFragment<FragmentUsernameBinding, UsernameView, UsernamePresenter>(), UsernameView {

    private var navController: NavController? = null
    private var eventHandler: EventHandler? = null

    override fun setUpView(viewBind: FragmentUsernameBinding) {
        navController = findNavController()
        eventHandler = EventHandler(listOf(SnackbarEventManager(
            eventView = viewBind.root,
            duration = Snackbar.LENGTH_INDEFINITE,
            actionWithName = { } to getString(R.string.ok)
        )), viewBind.loadingIndicator)
    }

    override fun destroyView() {
        navController = null
        eventHandler = null
    }

    override fun bindView(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentUsernameBinding = FragmentUsernameBinding.inflate(layoutInflater, container, false)

    override fun getPresenterView(): UsernameView = this

    override fun usernameChangedObservable(): Observable<String> =
        viewBind!!.usernameInput.debouncedTextChanges()

    override fun finishClickedObservable(): Observable<Unit> =
        viewBind!!.finishButton.throttleClicks()

    override fun handleValidationError(error: Option<ValidationError.EmptyField>) {
        viewBind!!.usernameLayout.setErrorOption(error)
    }

    override fun handleChangeNameEvent(event: Option<BaseEvent>) {
        viewBind!!.finishButton.apply {
            text = if (event.orNull() == LoadingEvent) "" else getString(R.string.finish)
            isClickable = event.orNull() != LoadingEvent
            isFocusable = event.orNull() != LoadingEvent
            isEnabled = event.orNull() != LoadingEvent
        }
        eventHandler!!.invoke(event)
    }

    override fun finish() {
        startActivity(MainActivity.newIntent(requireContext()))
        requireActivity().finishAffinity()
    }
}