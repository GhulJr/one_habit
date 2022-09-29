package com.ghuljr.onehabit_data.storage.persistence

import arrow.core.firstOrNone
import com.ghuljr.onehabit_data.base.storage.BaseDatabase
import com.ghuljr.onehabit_data.cache.synchronisation.DataSource
import com.ghuljr.onehabit_data.storage.model.MilestoneEntity
import com.ghuljr.onehabit_data.storage.model.MilestoneEntityHolder
import com.ghuljr.onehabit_data.storage.model.MilestoneEntityHolder_
import com.ghuljr.onehabit_data.storage.model.MilestoneEntity_
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

}