package com.ghuljr.onehabit_data.network.service

import arrow.core.Either
import com.ghuljr.onehabit_data.network.model.MilestoneResponse
import com.ghuljr.onehabit_error.BaseError
import io.reactivex.rxjava3.core.Maybe

interface MilestoneService {

    fun getMilestoneById(
        userId: String,
        milestoneId: String
    ): Maybe<Either<BaseError, MilestoneResponse>>

    fun resolveMilestone(
        userId: String,
        milestoneId: String,
    ): Maybe<Either<BaseError, MilestoneResponse>>
}