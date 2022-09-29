package com.ghuljr.onehabit_data.repository

import com.ghuljr.onehabit_data.cache.memory.MemoryCache
import com.ghuljr.onehabit_data.cache.synchronisation.DataSource
import com.ghuljr.onehabit_data.network.service.MilestoneService
import com.ghuljr.onehabit_data.storage.model.MilestoneEntity
import com.ghuljr.onehabit_data.storage.persistence.MilestoneDatabase
import com.ghuljr.onehabit_tools.di.ComputationScheduler
import com.ghuljr.onehabit_tools.di.NetworkScheduler
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
            fresh = {  },
            networkScheduler = networkScheduler,
            computationScheduler = computationScheduler
        )
    }
}