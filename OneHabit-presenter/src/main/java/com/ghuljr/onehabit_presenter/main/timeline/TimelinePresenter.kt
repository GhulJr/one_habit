package com.ghuljr.onehabit_presenter.main.timeline

import arrow.core.left
import com.ghuljr.onehabit_data.domain.Goal
import com.ghuljr.onehabit_data.repository.GoalRepository
import com.ghuljr.onehabit_error.BaseEvent
import com.ghuljr.onehabit_error.LoadingEvent
import com.ghuljr.onehabit_presenter.base.BasePresenter
import com.ghuljr.onehabit_tools.di.FragmentScope
import com.ghuljr.onehabit_tools.di.UiScheduler
import com.ghuljr.onehabit_tools.extension.mapLeft
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.disposables.SerialDisposable
import java.util.*
import javax.inject.Inject

// TODO: handle errors
// TODO: handle loading
// TODO: handle swipe refresh
@FragmentScope
class TimelinePresenter @Inject constructor(
    @UiScheduler private val uiScheduler: Scheduler,
    private val goalRepository: GoalRepository
) : BasePresenter<TimelineView>() {

    override fun subscribeToView(view: TimelineView): Disposable = SerialDisposable(
        goalRepository.currentGoals
            .mapLeft { it as BaseEvent }
            .startWithItem(LoadingEvent.left())
            .observeOn(uiScheduler)
            .subscribe {
                it.fold(
                    ifRight = { goals ->
                        view.submitItems(
                            listOf(HeaderItem)
                                .plus(goals.map { it.toItem() })
                                .plus(
                                    SummaryItem(
                                        dayNumber = (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1) % 7,
                                        totalDays = goals.size
                                    )
                                )
                        )
                    },
                    ifLeft = { /* TODO: handle errors and loading */ }
                )
            }
    )

    private fun Goal.toItem() = GoalItem(
        id = id,
        dayNumber = dayNumber.toInt() + 1,
        state = getState()
    )

    private fun Goal.getState(): GoalItem.State {
        val currentDayNumber = (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1) % 7 - 1
        return when {
            dayNumber.toInt() == currentDayNumber -> GoalItem.State.Today
            dayNumber < currentDayNumber -> {
                // TODO: encode states, add something like score
                GoalItem.State.Past.Success
            }
            else -> GoalItem.State.Future
        }
    }
}