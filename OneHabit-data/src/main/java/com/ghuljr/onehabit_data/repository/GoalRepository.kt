package com.ghuljr.onehabit_data.repository

import com.ghuljr.onehabit_data.cache.memory.MemoryCache
import com.ghuljr.onehabit_data.cache.synchronisation.DataSource
import com.ghuljr.onehabit_data.network.model.GoalResponse
import com.ghuljr.onehabit_data.network.service.GoalsService
import com.ghuljr.onehabit_data.storage.model.GoalEntity
import com.ghuljr.onehabit_data.storage.persistence.GoalDatabase
import com.ghuljr.onehabit_tools.di.ComputationScheduler
import com.ghuljr.onehabit_tools.di.NetworkScheduler
import com.ghuljr.onehabit_tools.extension.mapRight
import io.reactivex.rxjava3.core.Scheduler
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoalRepository @Inject constructor(
    @ComputationScheduler private val computationScheduler: Scheduler,
    @NetworkScheduler private val networkScheduler: Scheduler,
    private val goalsService: GoalsService,
    private val goalsDatabase: GoalDatabase,
    private val milestoneGoalsCache: MemoryCache.Factory<String, DataSource<List<GoalEntity>>>
) {

    // TODO: swap current goal
    // TODO: if milestone is finished, then return summary
    // TODO: if habit is finished, then return screen with congrats, where user can select new habit and eventually keep current one

    private val cache = milestoneGoalsCache.create { key ->
        DataSource(
            refreshInterval = 1,
            refreshIntervalUnit = TimeUnit.DAYS,
            cachedDataFlowable = goalsDatabase.goalsForMilestone(key.customKey!!),
            fetch = { goalsService.getGoalsForMilestone(key.customKey, key.userId).mapRight { it.toEntity() }.toSingle() },
            invalidateAndUpdate = { cacheWithTime -> goalsDatabase.replaceGoalsForMilestone(key.userId, key.customKey, cacheWithTime.dueToInMillis, cacheWithTime.value.orNull() ?: emptyList()) },
            networkScheduler = networkScheduler,
            computationScheduler = computationScheduler
        )
    }
}

private fun List<GoalResponse>.toEntity() = map { it.toEntity() }

private fun GoalResponse.toEntity() = GoalEntity(
    id = goalId,
    userId = userId,
    milestoneId = milestoneId,
    remindersAtMs = remindAtMs
)