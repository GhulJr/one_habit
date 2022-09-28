package com.ghuljr.onehabit_data.network.service

import arrow.core.Either
import com.ghuljr.onehabit_data.network.model.GoalResponse
import com.ghuljr.onehabit_error.BaseError
import io.reactivex.rxjava3.core.Maybe

interface GoalsService {
    fun getGoalsForMilestone(milestoneId: String, userId: String): Maybe<Either<BaseError, List<GoalResponse>>>
    fun setGoalsFinished(goalIds: List<String>, userId: String): Maybe<Either<BaseError, List<GoalResponse>>>
}