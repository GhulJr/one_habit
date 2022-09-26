package com.ghuljr.onehabit_data.repository

import arrow.core.Either
import arrow.core.right
import com.ghuljr.onehabit_data.cache.memory.MemoryCache
import com.ghuljr.onehabit_data.cache.synchronisation.DataSource
import com.ghuljr.onehabit_data.domain.Goal
import com.ghuljr.onehabit_data.domain.UserMetadata
import com.ghuljr.onehabit_data.network.model.GoalResponse
import com.ghuljr.onehabit_data.network.service.GoalsService
import com.ghuljr.onehabit_data.storage.model.GoalEntity
import com.ghuljr.onehabit_data.storage.persistence.GoalDatabase
import com.ghuljr.onehabit_error.BaseError
import com.ghuljr.onehabit_tools.di.ComputationScheduler
import com.ghuljr.onehabit_tools.di.NetworkScheduler
import com.ghuljr.onehabit_tools.extension.mapRight
import com.ghuljr.onehabit_tools.extension.switchMapRightWithEither
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoalRepository @Inject constructor(
    @ComputationScheduler private val computationScheduler: Scheduler,
    @NetworkScheduler private val networkScheduler: Scheduler,
    private val goalsService: GoalsService,
    private val goalsDatabase: GoalDatabase,
    private val milestoneGoalsCache: MemoryCache.Factory<String, DataSource<List<GoalEntity>>>,
    private val userMetadataRepository: UserMetadataRepository
) {

    // TODO: swap current goal
    // TODO: if milestone is finished, then return summary
    // TODO: if habit is finished, then return screen with congrats, where user can select new habit and eventually keep current one

    private val cache = milestoneGoalsCache.create { key ->
        DataSource(
            refreshInterval = 1,
            refreshIntervalUnit = TimeUnit.DAYS,
            cachedDataFlowable = goalsDatabase.goalsForMilestoneSorted(key.customKey!!),
            fetch = { goalsService.getGoalsForMilestone(key.customKey, key.userId).mapRight { it.toEntity() }.toSingle() },
            invalidateAndUpdate = { cacheWithTime -> goalsDatabase.replaceGoalsForMilestone(key.userId, key.customKey, cacheWithTime.dueToInMillis, cacheWithTime.value.orNull() ?: emptyList()) },
            networkScheduler = networkScheduler,
            computationScheduler = computationScheduler
        )
    }

    fun goalsForMilestone(milestoneId: String): Observable<Either<BaseError, List<Goal>>> = userMetadataRepository.currentUser
        .switchMapRightWithEither { user ->
            cache[user.milestoneId!!]
                .switchMapRightWithEither { source ->
                    source
                        .dataFlowable
                        .mapRight { it.toDomain() }
                }
                .toObservable()
        }
        .replay(1)
        .refCount()

    fun keepTrackOfCurrentGoal(milestoneId: String): Observable<Either<BaseError, UserMetadata>> = userMetadataRepository.currentUser
        .switchMapRightWithEither { user ->
            cache[user.milestoneId!!]
                .switchMapRightWithEither { source ->
                    source
                        .dataFlowable
                        .mapRight { it.toDomain() }
                }
                .toObservable()
                .switchMapRightWithEither { goals ->
                    // TODO: how to handle if the new milestone should be assigned
                    val todayDayNumber = (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1) % 7 - 1
                    val newGoal = goals[todayDayNumber]
                    if(newGoal.id == user.goalId)
                        Observable.just(user.right())
                    else
                        userMetadataRepository.setCurrentGoal(newGoal.id).toObservable()
                }
        }
}

private fun List<GoalResponse>.toEntity() = map { it.toEntity() }

private fun GoalResponse.toEntity() = GoalEntity(
    id = goalId,
    userId = userId,
    milestoneId = milestoneId,
    remindersAtMs = remindAtMs,
    dayNumber = dayNumber
)

private fun List<GoalEntity>.toDomain() = map { it.toDomain() }

private fun GoalEntity.toDomain() = Goal(
    id = id,
    userId = userId,
    milestoneId = milestoneId,
    remindAtMs = remindersAtMs,
    dayNumber = dayNumber
)