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
import com.ghuljr.onehabit_tools.extension.mapRight
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
    private val memoryCacheFactory: MemoryCache.Factory<String, DataSource<List<ActionEntity>>>
) {

    private val todayActionCache = memoryCacheFactory.create { key ->
        val actionsDb = actionsDatabaseFactory.create(key.userId)
        DataSource(
            refreshInterval = 1,
            refreshIntervalUnit = TimeUnit.DAYS,
            cachedDataFlowable = actionsDb.getActionsByGoalId(key.customKey!!),
            fetch = { actionsService.getActionsFromGoal(key.customKey).toSingle().mapRight { it.toStorageModel() } },
            invalidateAndUpdate = { newCache -> }
        )
    }
}

private fun List<ActionResponse>.toStorageModel() = map { it.toStorageModel() }

private fun ActionResponse.toStorageModel() = ActionEntity(
    userId = id,
    remindersAtMs = remindersAtMs?.map { it.toString() },
    currentRepeat = currentRepeat,
    totalRepeats = totalRepeats
)
