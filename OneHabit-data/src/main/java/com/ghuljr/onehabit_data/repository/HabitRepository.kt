package com.ghuljr.onehabit_data.repository

import com.ghuljr.onehabit_data.cache.memory.MemoryCache
import com.ghuljr.onehabit_data.cache.synchronisation.DataSource
import com.ghuljr.onehabit_data.network.model.HabitResponse
import com.ghuljr.onehabit_data.network.service.HabitService
import com.ghuljr.onehabit_data.storage.model.HabitEntity
import com.ghuljr.onehabit_data.storage.persistence.HabitDatabase
import com.ghuljr.onehabit_tools.di.ComputationScheduler
import com.ghuljr.onehabit_tools.di.NetworkScheduler
import com.ghuljr.onehabit_tools.extension.mapRight
import io.reactivex.rxjava3.core.Scheduler
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HabitRepository @Inject constructor(
    @ComputationScheduler private val computationScheduler: Scheduler,
    @NetworkScheduler private val networkScheduler: Scheduler,
    private val databaseFactory: HabitDatabase.Factory,
    private val habitService: HabitService,
    private val memoryCacheFactory: MemoryCache.Factory<String, DataSource<HabitEntity>>
) {

    private val cache = memoryCacheFactory.create { key ->
        val habitDb = databaseFactory.create(key.userId)
        DataSource(
            refreshInterval = 1,
            refreshIntervalUnit = TimeUnit.DAYS,
            cachedDataFlowable = habitDb.getHabit(key.customKey!!),
            fetch = {
                habitService.getHabit(userId = key.userId, habitId = key.customKey)
                    .toSingle()
                    .mapRight { it.toEntity() }
            },
            invalidateAndUpdate = { habit ->
                habitDb.replaceHabit(
                    habitId = key.customKey!!,
                    habit = habit.value.orNull(),
                    dueToMs = habit.dueToInMillis
                )
            },
            networkScheduler = networkScheduler,
            computationScheduler = computationScheduler
        )
    }
}

private fun HabitResponse.toEntity() = HabitEntity(
    userId = userId,
    id = id,
    currentProgress = currentProgress,
    defaultProgressFactor = defaultProgressFactor,
    defaultRemindersMs = defaultRemindersMs?.map { it.toString() },
    baseIntensity = baseIntensity,
    desiredIntensity = desiredIntensity,
    title = title,
    description = description,
    type = type,
    habitSubject = habitSubject,
    settlingFormat = settlingFormat
)