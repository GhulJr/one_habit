package com.ghuljr.onehabit_data.repository

import arrow.core.Either
import com.ghuljr.onehabit_data.cache.memory.MemoryCache
import com.ghuljr.onehabit_data.cache.synchronisation.DataSource
import com.ghuljr.onehabit_data.domain.Milestone
import com.ghuljr.onehabit_data.network.model.MilestoneResponse
import com.ghuljr.onehabit_data.network.service.MilestoneService
import com.ghuljr.onehabit_data.storage.model.MilestoneEntity
import com.ghuljr.onehabit_data.storage.persistence.MilestoneDatabase
import com.ghuljr.onehabit_error.BaseError
import com.ghuljr.onehabit_tools.di.ComputationScheduler
import com.ghuljr.onehabit_tools.di.NetworkScheduler
import com.ghuljr.onehabit_tools.extension.mapRight
import com.ghuljr.onehabit_tools.extension.switchMapRightWithEither
import io.objectbox.annotation.Index
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MilestoneRepository @Inject constructor(
    @NetworkScheduler private val networkScheduler: Scheduler,
    @ComputationScheduler private val computationScheduler: Scheduler,
    private val milestoneService: MilestoneService,
    private val milestoneDatabase: MilestoneDatabase,
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

    fun milestoneById(milestoneId: String): Observable<Either<BaseError, Milestone>> =
        milestoneCache[milestoneId]
            .switchMapRightWithEither {
                it.dataFlowable
                    .mapRight { it.toDomain() }
            }
            .toObservable()
            .replay(1)
            .refCount()
}

private fun MilestoneResponse.toEntity() = MilestoneEntity(
    id = id,
    userId = userId,
    intensity = intensity,
    orderNumber = orderNumber,
    resolved = resolved
)

private fun MilestoneEntity.toDomain() = Milestone(
    id = id,
    userId = userId,
    intensity = intensity,
    orderNumber = orderNumber,
    resolved = resolved
)