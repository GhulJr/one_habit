package com.ghuljr.onehabit_data.network.service

import arrow.core.Either
import com.ghuljr.onehabit_data.network.model.ActionRequest
import com.ghuljr.onehabit_data.network.model.GoalRequest
import com.ghuljr.onehabit_data.network.model.MilestoneRequest
import com.ghuljr.onehabit_data.network.model.MilestoneResponse
import com.ghuljr.onehabit_error.BaseError
import io.reactivex.rxjava3.core.Maybe

interface MilestoneService {

    fun getMilestoneById(
        userId: String,
        milestoneId: String
    ): Maybe<Either<BaseError, MilestoneResponse>>

    fun getMilestonesByHabitId(
        userId: String,
        habitId: String
    ): Maybe<Either<BaseError, List<MilestoneResponse>>>


    fun resolveMilestone(
        userId: String,
        milestoneId: String,
    ): Maybe<Either<BaseError, MilestoneResponse>>

    fun generateMilestone(
        milestoneRequest: MilestoneRequest,
        goalsRequest: List<GoalRequest>,
        actionRequest: ActionRequest,
        frequency: Int
    ): Maybe<Either<BaseError, MilestoneResponse>>
}