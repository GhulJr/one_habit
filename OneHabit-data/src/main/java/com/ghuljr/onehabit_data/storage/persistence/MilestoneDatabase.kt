package com.ghuljr.onehabit_data.storage.persistence

import com.ghuljr.onehabit_data.base.storage.BaseDatabase
import com.ghuljr.onehabit_data.storage.model.MilestoneEntity
import com.ghuljr.onehabit_data.storage.model.MilestoneEntityHolder
import com.ghuljr.onehabit_tools.di.ComputationScheduler
import io.objectbox.Box
import io.reactivex.rxjava3.core.Scheduler
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MilestoneDatabase @Inject constructor(
    override val box: Box<MilestoneEntity>,
    @ComputationScheduler override val computationScheduler: Scheduler,
    private val cacheBox: Box<MilestoneEntityHolder>
): BaseDatabase<MilestoneEntity>() {

}