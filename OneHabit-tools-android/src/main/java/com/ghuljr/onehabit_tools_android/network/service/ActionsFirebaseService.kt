package com.ghuljr.onehabit_tools_android.network.service

import com.ghuljr.onehabit_tools.base.network.ActionResponse
import com.ghuljr.onehabit_tools.base.network.ActionsService
import com.ghuljr.onehabit_tools.di.ComputationScheduler
import com.ghuljr.onehabit_tools.di.NetworkScheduler
import io.reactivex.rxjava3.core.Scheduler
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActionsFirebaseService @Inject constructor(
    @ComputationScheduler private val computationScheduler: Scheduler,
    @NetworkScheduler private val networkScheduler: Scheduler
) : ActionsService {

    override fun getTodayActions(): List<ActionResponse> {
        TODO("Not yet implemented")
    }
}