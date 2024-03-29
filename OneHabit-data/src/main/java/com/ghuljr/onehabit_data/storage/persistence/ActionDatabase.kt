package com.ghuljr.onehabit_data.storage.persistence

import arrow.core.*
import com.ghuljr.onehabit_data.base.storage.BaseDatabase
import com.ghuljr.onehabit_data.cache.synchronisation.DataSource
import com.ghuljr.onehabit_data.domain.Action
import com.ghuljr.onehabit_data.storage.model.ActionEntity
import com.ghuljr.onehabit_data.storage.model.ActionEntity_
import com.ghuljr.onehabit_data.storage.model.ActionOfGoalEntitiesHolder
import com.ghuljr.onehabit_data.storage.model.ActionOfGoalEntitiesHolder_
import com.ghuljr.onehabit_error.NoDataError
import com.ghuljr.onehabit_tools.di.ComputationScheduler
import com.ghuljr.onehabit_tools.extension.switchMapRightWithEither
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.objectbox.Box
import io.objectbox.query.QueryBuilder
import io.objectbox.rx3.RxQuery
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class ActionDatabase @Inject constructor(
    override val box: Box<ActionEntity>,
    private val cacheBox: Box<ActionOfGoalEntitiesHolder>,
    @ComputationScheduler override val computationScheduler: Scheduler,
) : BaseDatabase<ActionEntity>() {

    fun getActionById(actionId: String): Flowable<Option<ActionEntity>> = RxQuery.observable(
        box.query().equal(ActionEntity_.id, actionId, QueryBuilder.StringOrder.CASE_SENSITIVE).build()
    )
        .map { it.firstOrNone() }
        .toFlowable(BackpressureStrategy.BUFFER)
        .subscribeOn(computationScheduler)
        .share()

    fun getActionsByGoalId(goalId: String): Flowable<DataSource.CacheWithTime<List<ActionEntity>>> =
        RxQuery.observable(
            box.query().equal(ActionEntity_.goalId, goalId, QueryBuilder.StringOrder.CASE_SENSITIVE)
                .build()
        )
            .map { actions ->
                DataSource.CacheWithTime(
                    value = actions.some(),
                    dueToInMillis = cacheBox.query()
                        .equal(ActionOfGoalEntitiesHolder_.goalId, goalId, QueryBuilder.StringOrder.CASE_SENSITIVE)
                        .build().findUnique()?.dueToInMillis ?: 0L
                )
            }
            .toFlowable(BackpressureStrategy.BUFFER)
            .subscribeOn(computationScheduler)
            .share()

    fun removeAction(id: String) {
        box.query().equal(ActionEntity_.id, id, QueryBuilder.StringOrder.CASE_SENSITIVE)
            .build().remove()
    }

    fun removeActionsForGoal(goalId: String) {
        box.query().equal(ActionEntity_.goalId, goalId, QueryBuilder.StringOrder.CASE_SENSITIVE)
            .build().remove()
        cacheBox.query()
            .equal(ActionOfGoalEntitiesHolder_.goalId, goalId, QueryBuilder.StringOrder.CASE_SENSITIVE)
            .build().remove()
    }

    fun replaceActionsForGoal(
        goalId: String,
        userId: String,
        dueToInMillis: Long,
        actions: List<ActionEntity>
    ) {
        put(*actions.toTypedArray())
        cacheBox.put(
            ActionOfGoalEntitiesHolder(
                userId = userId,
                goalId = goalId,
                dueToInMillis = dueToInMillis
            )
        )
    }
}