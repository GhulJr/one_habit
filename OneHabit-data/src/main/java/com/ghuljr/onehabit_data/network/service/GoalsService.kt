package com.ghuljr.onehabit_data.network.service

import arrow.core.Either
import com.ghuljr.onehabit_data.domain.Goal
import com.ghuljr.onehabit_data.network.model.GoalRequest
import com.ghuljr.onehabit_data.network.model.GoalResponse
import com.ghuljr.onehabit_error.BaseError
import io.reactivex.rxjava3.core.Maybe

interface GoalsService {
    fun getGoalsForMilestone(milestoneId: String, userId: String): Maybe<Either<BaseError, List<GoalResponse>>>
    fun setGoalsFinished(goalIds: List<String>, userId: String, milestoneId: String): Maybe<Either<BaseError, List<GoalResponse>>>

    fun putGoals(goalRequests: List<GoalRequest>, userId: String, milestoneId: String):  Maybe<Either<BaseError, List<GoalResponse>>>
}