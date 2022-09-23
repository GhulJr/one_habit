package com.ghuljr.onehabit_data.storage.persistence

import com.ghuljr.onehabit_data.base.storage.BaseDatabase
import com.ghuljr.onehabit_data.storage.model.ActionOfGoalEntitiesHolder
import com.ghuljr.onehabit_data.storage.model.ActionOfGoalEntitiesHolder_
import com.ghuljr.onehabit_tools.di.ComputationScheduler
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.objectbox.Box
import io.objectbox.query.QueryBuilder
import io.objectbox.rx3.RxQuery
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Scheduler


class ActionDatabase @AssistedInject constructor(
    override val box: Box<ActionOfGoalEntitiesHolder>,
    @ComputationScheduler override val computationScheduler: Scheduler,
    @Assisted override val userId: String
) : BaseDatabase<ActionOfGoalEntitiesHolder>() {

    override val dataFlowable: Flowable<List<ActionOfGoalEntitiesHolder>> = RxQuery.observable(
        box.query()
            .equal(ActionOfGoalEntitiesHolder_.userId, userId, QueryBuilder.StringOrder.CASE_SENSITIVE)
            .build()
    )
        .toFlowable(BackpressureStrategy.BUFFER)
        .subscribeOn(computationScheduler)


    @AssistedFactory
    interface Factory {
        fun create(userId: String): ActionDatabase
    }
}