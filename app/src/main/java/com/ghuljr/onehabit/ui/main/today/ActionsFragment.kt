package com.ghuljr.onehabit.ui.main.today

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import arrow.core.toOption
import com.ghuljr.onehabit.R
import com.ghuljr.onehabit.databinding.FragmentTodayBinding
import com.ghuljr.onehabit.ui.add_action.AddActionActivity
import com.ghuljr.onehabit.ui.base.BaseFragment
import com.ghuljr.onehabit.ui.main.MainActivity
import com.ghuljr.onehabit.ui.main.today.info.ActionInfoBottomSheetDialog
import com.ghuljr.onehabit.ui.main.today.list.*
import com.ghuljr.onehabit.ui.notifications.NotificationsBroadcastReceiver
import com.ghuljr.onehabit_error.BaseError
import com.ghuljr.onehabit_error_android.event_manager.SnackbarEventManager
import com.ghuljr.onehabit_presenter.main.MainStep
import com.ghuljr.onehabit_presenter.main.today.TodayItem
import com.ghuljr.onehabit_presenter.main.today.ActionsPresenter
import com.ghuljr.onehabit_presenter.main.today.ActionsView
import com.ghuljr.onehabit_presenter.main.today.info.ActionInfoPresenter
import com.ghuljr.onehabit_tools_android.base.list.ItemListAdapter
import com.ghuljr.onehabit_tools_android.tool.ItemDivider
import com.google.android.material.snackbar.Snackbar
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import javax.inject.Inject

class ActionsFragment : BaseFragment<FragmentTodayBinding, ActionsView, ActionsPresenter>(),
    ActionsView {

    @Inject lateinit var actionInfoPresenter: ActionInfoPresenter

    private var alarmManager: AlarmManager? = null


    private val todayAdapter = ItemListAdapter(
        TodayActionViewHolderManager(),
        TodayActionFinishedViewHolderManager(),
        AddActionViewHolderManager(),
        CustomActionViewHolderManager(),
        TodayHeaderViewHolderManager()
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as? MainActivity)?.setCurrentStep(MainStep.TODAY)

        alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as? AlarmManager?
        alarmManager?.setTimeZone("GMT")


        presenter.init(arguments?.getString(EXTRA_GOAL_ID).toOption())

        viewBind.apply {
            todayRecyclerView.apply {
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                adapter = todayAdapter
                addItemDecoration(
                    ItemDivider(
                        spanCount = 1, resources.getDimension(R.dimen.default_margin).toInt()
                    )
                )
            }
            swipeRefresh.setOnRefreshListener { presenter.refresh() }
            errorWidget.setOnRetryClickListener { presenter.refresh() }
        }
    }

    override fun onDestroyView() {
        alarmManager = null
        super.onDestroyView()
    }

    override fun bindView(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTodayBinding = FragmentTodayBinding.inflate(layoutInflater, container, false)

    override fun getPresenterView(): ActionsView = this

    override fun openDetails(actionId: String) {
        ActionInfoBottomSheetDialog(actionInfoPresenter, actionId).show(childFragmentManager, null)
    }

    override fun submitItems(items: List<TodayItem>) {
        todayAdapter.submitList(items)
    }

    override fun handleItemsError(error: BaseError?) {
        val snackbarErrorManager = SnackbarEventManager(
            eventView = viewBind.root,
            duration = Snackbar.LENGTH_INDEFINITE,
            actionWithName = { } to getString(R.string.ok),
            anchorView = viewBind.container
        )
        if (todayAdapter.currentList.isEmpty())
            viewBind.errorWidget.handleError(error)
        else if (error != null) snackbarErrorManager.handleEvent(error.toOption())
    }

    override fun handleLoading(loading: Boolean) {
        viewBind.apply {
            swipeRefresh.isRefreshing = false
            loadingIndicator.isVisible = loading
        }
    }

    override fun openCreateCustomAction(goalId: String) {
        startActivity(AddActionActivity.intent(requireContext(), goalId))
    }

    override fun setNotification(id: String, notifications: List<Long>) {
        notifications.forEach { time ->
            val alarmIntent = Intent(requireContext(), NotificationsBroadcastReceiver::class.java)
                .apply { data = Uri.parse("timer:$time") }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                time.toInt(),
                alarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

            val localTime = LocalDateTime.now(ZoneId.of("GMT"))
            val rtcTime = Calendar.getInstance(TimeZone.getTimeZone("GMT")).timeInMillis
            val notificationTime = localTime.run { rtcTime - toLocalTime().toSecondOfDay() * 1000 } + time

            if (notificationTime > rtcTime)

                alarmManager?.set(
                    AlarmManager.RTC_WAKEUP,
                    notificationTime,
                    pendingIntent
                )
        }

    }

    companion object {

        private const val EXTRA_GOAL_ID = "extra_goal_id"

        fun instance(goalId: String?) = ActionsFragment().apply {
            arguments = Bundle().apply {
                putString(EXTRA_GOAL_ID, goalId)
            }
        }
    }
}