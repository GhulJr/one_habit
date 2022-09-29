package com.ghuljr.onehabit_tools_android.network.service

import com.ghuljr.onehabit_data.network.service.MilestoneService
import com.ghuljr.onehabit_tools.di.NetworkScheduler
import io.reactivex.rxjava3.core.Scheduler
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MilestoneFirebaseService @Inject constructor(
    @NetworkScheduler private val networkScheduler: Scheduler
) : MilestoneService {
}