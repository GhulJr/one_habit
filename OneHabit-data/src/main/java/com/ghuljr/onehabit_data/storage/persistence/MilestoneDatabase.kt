package com.ghuljr.onehabit_data.storage.persistence

import arrow.core.firstOrNone
import com.ghuljr.onehabit_data.base.storage.BaseDatabase
import com.ghuljr.onehabit_data.cache.synchronisation.DataSource
import com.ghuljr.onehabit_data.storage.model.*
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
class MilestoneDatabase @Inject constructor(
    override val box: Box<MilestoneEntity>,
    @ComputationScheduler override val computationScheduler: Scheduler,
    private val cacheBox: Box<MilestoneEntityHolder>
): BaseDatabase<MilestoneEntity>() {

    fun getMilestoneById(milestoneId: String): Flowable<DataSource.CacheWithTime<MilestoneEntity>> = RxQuery.observable(
        box.query()
            .equal(MilestoneEntity_.id, milestoneId, QueryBuilder.StringOrder.CASE_SENSITIVE)
            .build()
    )
        .map {
            DataSource.CacheWithTime(
                value = it.firstOrNone(),
                dueToInMillis = cacheBox.query()
                    .equal(MilestoneEntityHolder_.milestoneId, milestoneId, QueryBuilder.StringOrder.CASE_SENSITIVE)
                    .build()
                    .findUnique()
                    ?.dueToInMillis ?: 0L
            )
        }
        .toFlowable(BackpressureStrategy.BUFFER)
        .subscribeOn(computationScheduler)

    fun removeMilestone(milestoneId: String) {
        box.query().equal(MilestoneEntity_.id, milestoneId, QueryBuilder.StringOrder.CASE_SENSITIVE).build().remove()
    }

    fun replaceMilestone(milestoneId: String, milestone: MilestoneEntity?, dueToMs: Long) {
        if(milestone == null)
            removeMilestone(milestoneId)
        else {
            put(milestone)
            cacheBox.put(
                MilestoneEntityHolder(
                    milestoneId = milestoneId,
                    userId = milestone.userId,
                    dueToInMillis = dueToMs
                )
            )
        }
    }
}