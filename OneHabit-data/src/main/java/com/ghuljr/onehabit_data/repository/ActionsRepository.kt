package com.ghuljr.onehabit_data.repository

import arrow.core.some
import com.ghuljr.onehabit_data.cache.memory.MemoryCache
import com.ghuljr.onehabit_data.cache.synchronisation.DataSource
import com.ghuljr.onehabit_data.network.model.ActionResponse
import com.ghuljr.onehabit_data.network.service.ActionsService
import com.ghuljr.onehabit_data.storage.model.ActionEntity
import com.ghuljr.onehabit_data.storage.persistence.ActionDatabase
import com.ghuljr.onehabit_tools.di.ComputationScheduler
import com.ghuljr.onehabit_tools.di.NetworkScheduler
import com.ghuljr.onehabit_tools.extension.switchMapRight
import com.ghuljr.onehabit_tools.extension.switchMapRightWithEither
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Scheduler
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/* TODO: add custom database and add it to datasource */
@Singleton
class ActionsRepository @Inject constructor(
    @NetworkScheduler private val networkScheduler: Scheduler,
    @ComputationScheduler private val computationScheduler: Scheduler,
    private val actionsService: ActionsService,
    private val actionsDatabaseFactory: ActionDatabase.Factory,
    private val loggedInUserRepository: LoggedInUserRepository,
    memoryCacheFactory: MemoryCache.Factory<Unit, ActionDatabase>
) {

    private val todayActionCache = memoryCacheFactory.create { actionsDatabaseFactory.create(it.userId) }

/*    private val source = DataSource(
        refreshInterval = 1,
        refreshIntervalUnit = TimeUnit.DAYS,
        cachedDataFlowable = todayActionCache.get()
            .switchMapRight { database ->
                database.dataFlowable
                    .map { list ->
                        DataSource.CacheWithTime(list.some(), list.firstOrNull()?.dueToInMillis ?: 0L)
                    }
            },
        fetch = () -> { actionsService.getActionsFromGoal().toSingle().map {  } }
    )*/
}

/*
private fun ActionResponse.toStorageModel() = ActionEntity(
    userId = id,
    remindersAtMs = remindersAtMs?.map { it.toString() },
    currentRepeat = currentRepeat,
    totalRepeats = totalRepeats
)*/
