package com.ghuljr.onehabit_data.storage.persistence

import arrow.core.firstOrNone
import com.ghuljr.onehabit_data.base.storage.BaseDatabase
import com.ghuljr.onehabit_data.cache.synchronisation.DataSource
import com.ghuljr.onehabit_data.storage.model.HabitEntity
import com.ghuljr.onehabit_data.storage.model.HabitEntityHolder
import com.ghuljr.onehabit_data.storage.model.HabitEntityHolder_
import com.ghuljr.onehabit_data.storage.model.HabitEntity_
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

class HabitDatabase @AssistedInject constructor(
    @Assisted override val userId: String,
    override val box: Box<HabitEntity>,
    val cacheBox: Box<HabitEntityHolder>,
    @ComputationScheduler override val computationScheduler: Scheduler
) : BaseDatabase<HabitEntity>() {


    fun getHabit(habitId: String): Flowable<DataSource.CacheWithTime<HabitEntity>> =
        RxQuery.observable(
            box.query().equal(HabitEntity_.id, habitId, QueryBuilder.StringOrder.CASE_SENSITIVE)
                .build()
        ).map { habits ->
            DataSource.CacheWithTime(
                value = habits.firstOrNone(),
                dueToInMillis = cacheBox.query()
                    .equal(HabitEntityHolder_.habitId, habitId, QueryBuilder.StringOrder.CASE_SENSITIVE)
                    .build()
                    .findUnique()
                    ?.dueToInMillis ?: 0L
            )
        }
            .toFlowable(BackpressureStrategy.BUFFER)
            .subscribeOn(computationScheduler)

    @AssistedFactory
    interface Factory {
        fun create(userId: String): HabitDatabase
    }
}