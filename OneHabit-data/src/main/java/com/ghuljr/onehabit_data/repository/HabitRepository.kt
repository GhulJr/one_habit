package com.ghuljr.onehabit_data.repository

import arrow.core.Either
import arrow.core.getOrElse
import com.ghuljr.onehabit_data.cache.memory.MemoryCache
import com.ghuljr.onehabit_data.cache.synchronisation.DataSource
import com.ghuljr.onehabit_data.domain.Habit
import com.ghuljr.onehabit_data.network.model.HabitResponse
import com.ghuljr.onehabit_data.network.service.HabitService
import com.ghuljr.onehabit_data.storage.model.HabitEntity
import com.ghuljr.onehabit_data.storage.persistence.HabitDatabase
import com.ghuljr.onehabit_error.BaseError
import com.ghuljr.onehabit_tools.di.ComputationScheduler
import com.ghuljr.onehabit_tools.di.NetworkScheduler
import com.ghuljr.onehabit_tools.extension.mapRight
import com.ghuljr.onehabit_tools.extension.switchMapRightWithEither
import com.ghuljr.onehabit_tools.model.HabitTopic
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HabitRepository @Inject constructor(
    @ComputationScheduler private val computationScheduler: Scheduler,
    @NetworkScheduler private val networkScheduler: Scheduler,
    private val habitDatabase: HabitDatabase,
    private val habitService: HabitService,
    private val userMetadataRepository: UserMetadataRepository,
    private val memoryCacheFactory: MemoryCache.Factory<String, DataSource<HabitEntity>>
) {

    private val cache = memoryCacheFactory.create { key ->
        DataSource(
            refreshInterval = 1,
            refreshIntervalUnit = TimeUnit.DAYS,
            cachedDataFlowable = habitDatabase.getHabit(key.customKey!!),
            fetch = {
                habitService.getHabit(userId = key.userId, habitId = key.customKey)
                    .toSingle()
                    .mapRight { it.toEntity() }
            },
            invalidateAndUpdate = { habit ->
                habitDatabase.replaceHabit(
                    habitId = key.customKey!!,
                    habit = habit.value.orNull(),
                    dueToMs = habit.dueToInMillis
                )
            },
            networkScheduler = networkScheduler,
            computationScheduler = computationScheduler
        )
    }

    val todayHabitObservable: Observable<Either<BaseError, Habit>> = userMetadataRepository.currentUser
        .filter { it.map { it.habitId != null }.getOrElse { true } }
        .switchMapRightWithEither { currentUser ->
            cache[currentUser.habitId!!]
                .switchMapRightWithEither { it.dataFlowable }
                .toObservable()
                .mapRight { it.toDomain() }

        }
        .replay(1)
        .refCount()
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

private fun HabitEntity.toDomain() = Habit(
    userId = userId,
    id = id,
    currentProgress = currentProgress,
    defaultProgressFactor = defaultProgressFactor,
    defaultRemindersMs = defaultRemindersMs?.map { it.toLong() },
    baseIntensity = baseIntensity,
    desiredIntensity = desiredIntensity,
    title = title,
    description = description,
    type = HabitTopic.values().first { it.codeName == type },
    habitSubject = habitSubject,
    settlingFormat = settlingFormat
)