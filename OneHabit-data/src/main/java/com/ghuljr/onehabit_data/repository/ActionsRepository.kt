package com.ghuljr.onehabit_data.repository

import arrow.core.Either
import arrow.core.getOrElse
import com.ghuljr.onehabit_data.cache.memory.MemoryCache
import com.ghuljr.onehabit_data.cache.synchronisation.DataSource
import com.ghuljr.onehabit_data.network.model.ActionResponse
import com.ghuljr.onehabit_data.network.service.ActionsService
import com.ghuljr.onehabit_data.storage.model.ActionEntity
import com.ghuljr.onehabit_data.storage.persistence.ActionDatabase
import com.ghuljr.onehabit_error.BaseError
import com.ghuljr.onehabit_error.LoggedOutError
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
    private val actionsDatabaseFactory: ActionDatabase.Factory,
    private val loggedInUserRepository: LoggedInUserRepository,
    private val memoryCacheFactory: MemoryCache.Factory<String, DataSource<List<ActionEntity>>>
) {

    private val todayActionCache = memoryCacheFactory.create { key ->
        val actionsDb = actionsDatabaseFactory.create(key.userId)
        DataSource(
            refreshInterval = 1,
            refreshIntervalUnit = TimeUnit.HOURS,
            cachedDataFlowable = actionsDb.getActionsByGoalId(key.customKey!!),
            fetch = {
                actionsService.getActionsFromGoal(key.customKey, key.userId).toSingle()
                    .mapRight { it.toStorageModel(key.customKey, key.userId) }
            },
            invalidateAndUpdate = { newCache ->
                actionsDb.replaceActionsForGoal(
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

    // TODO: change it to list of domain models
    val todayActionsObservable: Observable<Either<BaseError, List<ActionEntity>>> = loggedInUserRepository.userIdFlowable
        .toEither { LoggedOutError as BaseError }
        .switchMapRightWithEither { userId ->
            // TODO: handle real goal id
            todayActionCache["-NC_DaicwoCWk5_qQ6Uh"]
                .mapLeft { it as BaseError }
                .switchMapRightWithEither { source -> source.dataFlowable }
        }
        .toObservable()

    fun refreshTodayActions(): Maybe<Either<BaseError, List<ActionEntity>>> = loggedInUserRepository.userIdFlowable
        .toEither { LoggedOutError as BaseError }
        .switchMapRightWithEither { userId ->
            todayActionCache["-NC_DaicwoCWk5_qQ6Uh"]
                .mapLeft { it as BaseError }
                .switchMapMaybeRightWithEither { source -> source.refresh() }
        }
        .firstElement()
}

private fun List<ActionResponse>.toStorageModel(goalId: String, userId: String) = map { it.toStorageModel(goalId, userId) }

private fun ActionResponse.toStorageModel(goalId: String, userId: String) = ActionEntity(
    id = id,
    userId = userId,
    remindersAtMs = remindersAtMs?.map { it.toString() },
    currentRepeat = currentRepeat,
    totalRepeats = totalRepeats,
    goalId = goalId
)
