package com.ghuljr.onehabit_data.storage.persistence

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import arrow.core.some
import com.ghuljr.onehabit_data.base.storage.BaseDatabase
import com.ghuljr.onehabit_data.cache.synchronisation.DataSource
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


class ActionDatabase @AssistedInject constructor(
    override val box: Box<ActionEntity>,
    private val cacheBox: Box<ActionOfGoalEntitiesHolder>,
    @ComputationScheduler override val computationScheduler: Scheduler,
    @Assisted override val userId: String
) : BaseDatabase<ActionEntity>() {

    override val dataFlowable: Flowable<List<ActionEntity>> = RxQuery.observable(
        box.query()
            .equal(ActionEntity_.userId, userId, QueryBuilder.StringOrder.CASE_SENSITIVE)
            .build()
    )
        .toFlowable(BackpressureStrategy.BUFFER)
        .subscribeOn(computationScheduler)

    fun getActionsByGoalId(goalId: String): Flowable<Either<NoDataError, DataSource.CacheWithTime<List<ActionEntity>>>> =
        RxQuery.observable<ActionEntity>(box.query().equal(ActionEntity_.goalId, goalId).build())
            .map { actions ->
                if (actions.isEmpty()) NoDataError.left()
                else DataSource.CacheWithTime(
                    value = actions.some(),
                    dueToInMillis = cacheBox.query()
                        .equal(ActionOfGoalEntitiesHolder_.goalId, goalId, QueryBuilder.StringOrder.CASE_SENSITIVE)
                        .build().findUnique()?.dueToInMillis ?: 0L
                ).right()
            }
            .toFlowable(BackpressureStrategy.BUFFER)
            .subscribeOn(computationScheduler)


    @AssistedFactory
    interface Factory {
        fun create(userId: String): ActionDatabase
    }
}