package com.ghuljr.onehabit_data.repository

import com.ghuljr.onehabit_data.cache.synchronisation.DataSource
import com.ghuljr.onehabit_data.network.service.ActionsService
import com.ghuljr.onehabit_tools.di.ComputationScheduler
import com.ghuljr.onehabit_tools.di.NetworkScheduler
import io.reactivex.rxjava3.core.Scheduler
import javax.inject.Inject
import javax.inject.Singleton

/* TODO: add custom database and add it to datasource */
@Singleton
class ActionsRepository @Inject constructor(
    @NetworkScheduler private val networkScheduler: Scheduler,
    @ComputationScheduler private val computationScheduler: Scheduler,
    private val actionsService: ActionsService
    //memoryCacheFactory: MemoryCache.Factory<Unit, List<ActionResponse>> // TODO: by default the key should be day id. Actions might be separated, though
) {

   /* private val source = DataSource(

    )*/
}