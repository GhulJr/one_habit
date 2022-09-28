package com.ghuljr.onehabit_data.repository

import arrow.core.Either
import arrow.core.right
import com.ghuljr.onehabit_data.cache.memory.MemoryCache
import com.ghuljr.onehabit_data.cache.synchronisation.DataSource
import com.ghuljr.onehabit_data.domain.Goal
import com.ghuljr.onehabit_data.network.model.GoalResponse
import com.ghuljr.onehabit_data.network.service.GoalsService
import com.ghuljr.onehabit_data.storage.model.GoalEntity
import com.ghuljr.onehabit_data.storage.persistence.GoalDatabase
import com.ghuljr.onehabit_error.BaseError
import com.ghuljr.onehabit_tools.di.ComputationScheduler
import com.ghuljr.onehabit_tools.di.NetworkScheduler
import com.ghuljr.onehabit_tools.extension.*
import io.reactivex.rxjava3.core.Maybe
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
            refreshIntervalUnit = TimeUnit.HOURS,
            cachedDataFlowable = goalsDatabase.goalsForMilestoneSorted(key.customKey!!),
            fetch = {
                goalsService.getGoalsForMilestone(key.customKey, key.userId)
                    .mapRight { it.toEntity() }.toSingle()
            },
            invalidateAndUpdate = { cacheWithTime -> goalsDatabase.replaceGoalsForMilestone(key.userId, key.customKey, cacheWithTime.dueToInMillis, cacheWithTime.value.orNull() ?: emptyList()) },
            networkScheduler = networkScheduler,
            computationScheduler = computationScheduler
        )
    }

    val currentGoals: Observable<Either<BaseError, List<Goal>>> = userMetadataRepository.currentUser
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

    val showMilestoneSummaryObservable: Observable<Unit> = userMetadataRepository.currentUser
        .switchMapRightWithEither { user ->
            if (user.milestoneId == null)
                Observable.just(true.right())
            else
                cache[user.milestoneId]
                    .switchMapRightWithEither { source ->
                        source
                            .dataFlowable
                            .mapRight { it.toDomain() }
                    }
                    .toObservable()
                    .switchMapRightWithEither { goals ->
                        val todayDayNumber = (Calendar.getInstance()
                            .get(Calendar.DAY_OF_WEEK) - 1) % 7 - 1
                        val newGoal = goals.sortedBy { it.dayNumber }
                            .filter { it.dayNumber >= todayDayNumber }.firstOrNull { !it.finished }
                        val newGoalIndex = goals.indexOfFirst { it.dayNumber == todayDayNumber.toLong() }
                        val goalsToFinish = goals.dropLast(goals.size - newGoalIndex)
                            .filterNot { it.finished }
                        when {
                            user.milestoneId == null || newGoal == null -> Observable.just(true.right())
                            newGoal.id == user.goalId -> Observable.just(false.right())
                            else -> if (goalsToFinish.isEmpty()) {
                                Maybe.just(Unit.right())
                            } else {
                                goalsService.setGoalsFinished(
                                    goalIds = goalsToFinish.map { it.id },
                                    userId = newGoal.userId,
                                    milestoneId = newGoal.milestoneId
                                ).mapRightWithEither { updatedGoals ->
                                    val entities = updatedGoals.toEntity()
                                    goalsDatabase.put(*entities.toTypedArray())
                                    Unit.right()
                                }
                            }
                                .flatMap {
                                    userMetadataRepository.setCurrentGoal(newGoal.id)
                                        .mapRight { false }
                                }
                                .toObservable()
                        }
                    }
        }
        .onlyRight()
        .filter { it }
        .toUnit()
        .replay(1)
        .refCount()

    fun refreshCurrentGoal(): Maybe<Either<BaseError, List<Goal>>> =
        userMetadataRepository.currentUser
            .firstElement()
            .flatMapRightWithEither { user ->
                cache[user.milestoneId!!]
                    .firstElement()
                    .flatMapRightWithEither { source ->
                        source.refresh()
                            .mapRight { it.toDomain() }
                    }
            }
}

private fun List<GoalResponse>.toEntity() = map { it.toEntity() }

private fun GoalResponse.toEntity() = GoalEntity(
    id = goalId,
    userId = userId,
    milestoneId = milestoneId,
    remindersAtMs = remindAtMs,
    dayNumber = dayNumber,
    finished = finished
)

private fun List<GoalEntity>.toDomain() = map { it.toDomain() }

private fun GoalEntity.toDomain() = Goal(
    id = id,
    userId = userId,
    milestoneId = milestoneId,
    remindAtMs = remindersAtMs,
    dayNumber = dayNumber,
    finished = finished
)