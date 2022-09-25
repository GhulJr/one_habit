package com.ghuljr.onehabit_data.repository

import arrow.core.Either
import arrow.core.getOrElse
import com.ghuljr.onehabit_data.cache.memory.MemoryCache
import com.ghuljr.onehabit_data.cache.synchronisation.DataSource
import com.ghuljr.onehabit_data.domain.Action
import com.ghuljr.onehabit_data.network.model.ActionResponse
import com.ghuljr.onehabit_data.network.service.ActionsService
import com.ghuljr.onehabit_data.storage.model.ActionEntity
import com.ghuljr.onehabit_data.storage.persistence.ActionDatabase
import com.ghuljr.onehabit_error.BaseError
import com.ghuljr.onehabit_error.NoDataError
import com.ghuljr.onehabit_tools.di.ComputationScheduler
import com.ghuljr.onehabit_tools.di.NetworkScheduler
import com.ghuljr.onehabit_tools.extension.*
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActionsRepository @Inject constructor(
    @NetworkScheduler private val networkScheduler: Scheduler,
    @ComputationScheduler private val computationScheduler: Scheduler,
    private val actionsService: ActionsService,
    private val actionsDatabase: ActionDatabase,
    private val userMetadataRepository: UserMetadataRepository,
    private val loggedInUserRepository: LoggedInUserRepository,
    private val memoryCacheFactory: MemoryCache.Factory<String, DataSource<List<ActionEntity>>>,
) {

    private val todayActionCache = memoryCacheFactory.create { key ->
        DataSource(
            refreshInterval = 1,
            refreshIntervalUnit = TimeUnit.HOURS,
            cachedDataFlowable = actionsDatabase.getActionsByGoalId(key.customKey!!),
            fetch = {
                actionsService.getActionsFromGoal(key.customKey, key.userId).toSingle()
                    .mapRight { it.toStorageModel(key.customKey, key.userId) }
            },
            invalidateAndUpdate = { newCache ->
                actionsDatabase.replaceActionsForGoal(
                    goalId = key.customKey,
                    userId = key.userId,
                    actions = newCache.value.getOrElse { listOf() },
                    dueToInMillis = newCache.dueToInMillis
                )
            },
            computationScheduler = computationScheduler,
            networkScheduler = networkScheduler
        )
    }

    val todayActionsObservable: Observable<Either<BaseError, List<Action>>> = userMetadataRepository.currentUser
        .filter { it.map { it.goalId != null }.getOrElse { true } }
        .switchMapRightWithEither { currentUser ->
            todayActionCache[currentUser.goalId!!]
                .switchMapRightWithEither { source -> source.dataFlowable }
                .toObservable()
                .mapRight { it.map { it.toDomain() } }
        }
        .replay(1)
        .refCount()

    fun refreshTodayActions(): Maybe<Either<BaseError, List<Action>>> =
        userMetadataRepository.currentUser
            .filter { it.map { it.goalId != null }.getOrElse { true } }
            .switchMapRightWithEither { currentUser ->
                todayActionCache[currentUser.goalId!!]
                    .switchMapMaybeRightWithEither { source -> source.refresh() }
                    .toObservable()
                    .mapRight { it.map { it.toDomain() } }
            }
            .firstElement()

    fun getActionObservable(actionId: String): Observable<Either<BaseError, Action>> =
        actionsDatabase.getActionById(actionId)
            .toObservable()
            .map { it.toEither { NoDataError as BaseError } }
            .mapRight { it.toDomain() }
            .replay(1)
            .refCount()

    fun completeAction(actionId: String, goalId: String): Maybe<Either<BaseError, Action>> =
        loggedInUserRepository.userIdFlowable
            .toEither { NoDataError as BaseError }
            .firstElement()
            .flatMapRightWithEither { userId ->
                actionsService.completeActionStep(actionId, userId)
                    .mapRight {
                        val storageModel = it.toStorageModel(goalId, userId)
                        actionsDatabase.put(storageModel)
                        storageModel.toDomain()
                    }
            }


    fun revertCompleteAction(actionId: String, goalId: String): Maybe<Either<BaseError, Action>> =  loggedInUserRepository.userIdFlowable
        .toEither { NoDataError as BaseError }
        .firstElement()
        .flatMapRightWithEither { userId ->
            actionsService.revertCompleteActionStep(actionId, userId)
                .mapRight {
                    val storageModel = it.toStorageModel(goalId, userId)
                    actionsDatabase.put(storageModel)
                    storageModel.toDomain()
                }
        }

}

private fun List<ActionResponse>.toStorageModel(goalId: String, userId: String) =
    map { it.toStorageModel(goalId, userId) }

private fun ActionResponse.toStorageModel(goalId: String, userId: String) = ActionEntity(
    id = id,
    userId = userId,
    remindersAtMs = remindersAtMs?.map { it.toString() },
    currentRepeat = currentRepeat,
    totalRepeats = totalRepeats,
    goalId = goalId,
    custom = custom
)

private fun ActionEntity.toDomain() = Action(
    id = id,
    goalId = goalId,
    repeatCount = currentRepeat,
    totalRepeats = totalRepeats,
    custom = custom,
    reminders = remindersAtMs?.map { it.toLong() }
)
