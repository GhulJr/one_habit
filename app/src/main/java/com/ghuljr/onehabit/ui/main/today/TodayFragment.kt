package com.ghuljr.onehabit.ui.main.today

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.ghuljr.onehabit.R
import com.ghuljr.onehabit.databinding.FragmentTodayBinding
import com.ghuljr.onehabit.ui.base.BaseFragment
import com.ghuljr.onehabit.ui.main.MainActivity
import com.ghuljr.onehabit.ui.main.today.info.ActionInfoBottomSheetDialog
import com.ghuljr.onehabit.ui.main.today.list.*
import com.ghuljr.onehabit_error.BaseError
import com.ghuljr.onehabit_presenter.main.MainStep
import com.ghuljr.onehabit_presenter.main.today.TodayItem
import com.ghuljr.onehabit_presenter.main.today.TodayPresenter
import com.ghuljr.onehabit_presenter.main.today.TodayView
import com.ghuljr.onehabit_presenter.main.today.info.ActionInfoPresenter
import com.ghuljr.onehabit_tools_android.base.list.ItemListAdapter
import com.ghuljr.onehabit_tools_android.tool.ItemDivider
import javax.inject.Inject

/*TODO: add view, when all items are done, that will allow to edit finished items :) */
// TODO: add loading
class TodayFragment : BaseFragment<FragmentTodayBinding, TodayView, TodayPresenter>(), TodayView {

    @Inject lateinit var actionInfoPresenter: ActionInfoPresenter

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

        viewBind.apply {
            todayRecyclerView.apply {
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                adapter = todayAdapter
                addItemDecoration(ItemDivider(spanCount = 1, resources.getDimension(R.dimen.default_margin).toInt()))
            }
            swipeRefresh.setOnRefreshListener { presenter.refresh() }
        }
    }

    override fun bindView(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTodayBinding = FragmentTodayBinding.inflate(layoutInflater, container, false)

    override fun getPresenterView(): TodayView = this

    override fun openDetails(actionId: String) {
        ActionInfoBottomSheetDialog(actionInfoPresenter, actionId).show(childFragmentManager, null)
    }

    override fun submitItems(items: List<TodayItem>) {
        todayAdapter.submitList(items)
    }

    override fun handleItemsError(error: BaseError) {
        //TODO("Not yet implemented")
    }

    override fun handleLoading(loading: Boolean) {
        viewBind.swipeRefresh.isRefreshing = loading
    }
}