package com.ghuljr.onehabit_data.storage.persistence

import arrow.core.some
import com.ghuljr.onehabit_data.base.storage.BaseDatabase
import com.ghuljr.onehabit_data.cache.synchronisation.DataSource
import com.ghuljr.onehabit_data.storage.model.GoalEntity
import com.ghuljr.onehabit_data.storage.model.GoalEntityHolder
import com.ghuljr.onehabit_data.storage.model.GoalEntityHolder_
import com.ghuljr.onehabit_data.storage.model.GoalEntity_
import com.ghuljr.onehabit_tools.di.ComputationScheduler
import io.objectbox.Box
import io.objectbox.query.QueryBuilder
import io.objectbox.rx3.RxQuery
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Scheduler
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoalDatabase @Inject constructor(
    @ComputationScheduler override val computationScheduler: Scheduler,
    override val box: Box<GoalEntity>,
    private val goalHolderBox: Box<GoalEntityHolder>
): BaseDatabase<GoalEntity>() {

    fun goalsForMilestone(milestoneId: String): Flowable<DataSource.CacheWithTime<List<GoalEntity>>> = RxQuery.observable(
        box.query().equal(GoalEntity_.milestoneId, milestoneId, QueryBuilder.StringOrder.CASE_SENSITIVE).build()
    )
        .map { goals -> DataSource.CacheWithTime(
            value = goals.some(),
            dueToInMillis = goalHolderBox.query()
                .equal(GoalEntityHolder_.milestoneId, milestoneId, QueryBuilder.StringOrder.CASE_SENSITIVE).build()
                .findUnique()?.dueToInMillis ?: 0L
        ) }
        .toFlowable(BackpressureStrategy.BUFFER)
        .subscribeOn(computationScheduler)
}