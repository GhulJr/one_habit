package com.ghuljr.onehabit_data.repository

import arrow.core.Either
import com.ghuljr.onehabit_data.cache.memory.MemoryCache
import com.ghuljr.onehabit_data.cache.synchronisation.DataSource
import com.ghuljr.onehabit_data.domain.Habit
import com.ghuljr.onehabit_data.domain.Milestone
import com.ghuljr.onehabit_data.network.model.ActionRequest
import com.ghuljr.onehabit_data.network.model.GoalRequest
import com.ghuljr.onehabit_data.network.model.MilestoneRequest
import com.ghuljr.onehabit_data.network.model.MilestoneResponse
import com.ghuljr.onehabit_data.network.service.MilestoneService
import com.ghuljr.onehabit_data.storage.model.MilestoneEntity
import com.ghuljr.onehabit_data.storage.persistence.MilestoneDatabase
import com.ghuljr.onehabit_error.BaseError
import com.ghuljr.onehabit_error.LoggedOutError
import com.ghuljr.onehabit_tools.di.ComputationScheduler
import com.ghuljr.onehabit_tools.di.NetworkScheduler
import com.ghuljr.onehabit_tools.extension.flatMapRightWithEither
import com.ghuljr.onehabit_tools.extension.mapRight
import com.ghuljr.onehabit_tools.extension.switchMapRightWithEither
import com.ghuljr.onehabit_tools.extension.toEither
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs

@Singleton
class MilestoneRepository @Inject constructor(
    @NetworkScheduler private val networkScheduler: Scheduler,
    @ComputationScheduler private val computationScheduler: Scheduler,
    private val milestoneService: MilestoneService,
    private val milestoneDatabase: MilestoneDatabase,
    private val userMetadataRepository: UserMetadataRepository,
    private val loggedInUserRepository: LoggedInUserRepository,
    private val cacheFactory: MemoryCache.Factory<String, DataSource<MilestoneEntity>>
) {

    private val milestoneCache = cacheFactory.create { key ->
        DataSource(
            refreshInterval = 1,
            refreshIntervalUnit = TimeUnit.DAYS,
            cachedDataFlowable = milestoneDatabase.getMilestoneById(key.customKey!!),
            fetch = {
                milestoneService.getMilestoneById(
                    userId = key.userId,
                    milestoneId = key.customKey
                )
                    .toSingle()
                    .mapRight { it.toEntity() }
            },
            invalidateAndUpdate = { milestoneDatabase.replaceMilestone(key.customKey, it.value.orNull(), it.dueToInMillis) },
            networkScheduler = networkScheduler,
            computationScheduler = computationScheduler
        )
    }

    val currentMilestoneObservable: Observable<Either<BaseError, Milestone>> = userMetadataRepository.currentUser
        .switchMapRightWithEither { user ->
            milestoneCache[user.milestoneId ?: ""].switchMapRightWithEither { it.dataFlowable }
                .mapRight { it.toDomain() }
                .toObservable()
        }
        .replay(1)
        .refCount()

    fun getMilestoneByIdObservable(milestoneId: String): Observable<Either<BaseError, Milestone>> =
        milestoneCache[milestoneId]
            .switchMapRightWithEither {
                it.dataFlowable
                    .mapRight { it.toDomain() }
            }
            .toObservable()
            .replay(1)
            .refCount()

    fun getMilestonesOfHabitObservable(habitId: String): Observable<Either<BaseError, List<Milestone>>> =
        loggedInUserRepository
            .userIdFlowable
            .toEither { LoggedOutError as BaseError }
            .toObservable()
            .switchMapRightWithEither { userId ->
                milestoneService.getMilestonesByHabitId(userId, habitId).toObservable()
                    .mapRight { it.map { it.toEntity().toDomain() } }
            }
            .replay(1)
            .refCount()

    fun generateMilestone(habit: Habit, intensity: Int): Maybe<Either<BaseError, Milestone>> {
        val request = MilestoneRequest(
            intensity = intensity,
            userId = habit.userId,
            habitId = habit.id
        )
        val goals = List(7) { dayNumber ->
            GoalRequest(
                userId = habit.userId,
                dayNumber = dayNumber.toLong(),
                finished = false
            )
        }
        val action = ActionRequest(
            userId = habit.userId,
            remindersAtMs = listOf(),
            currentRepeat = 0,
            totalRepeats = habit.run { abs((desiredIntensity - baseIntensity) * (intensity.toFloat() / 100) + baseIntensity) }
                .toInt(),
            customTitle = null
        )
        return milestoneService.generateMilestone(
            request, goals, action, habit.frequency
        )
            .flatMapRightWithEither { milestoneResponse ->
                userMetadataRepository.setCurrentMilestone(milestoneResponse.id)
                    .mapRight {
                        val entity = milestoneResponse.toEntity()
                        milestoneDatabase.put(entity)
                        entity.toDomain()
                    }
            }
    }
}

private fun MilestoneResponse.toEntity() = MilestoneEntity(
    id = id,
    userId = userId,
    intensity = intensity,
    resolvedAt = resolvedAt
)

private fun MilestoneEntity.toDomain() = Milestone(
    id = id,
    userId = userId,
    intensity = intensity,
    resolvedAt = resolvedAt
)