package com.ghuljr.onehabit_data.network.service

import arrow.core.Either
import com.ghuljr.onehabit_data.network.model.ActionResponse
import com.ghuljr.onehabit_data.network.model.UserMetadataResponse
import com.ghuljr.onehabit_error.BaseError
import io.reactivex.rxjava3.core.Maybe

interface UserService {

    fun getUserMetadata(userId: String) : Maybe<Either<BaseError, UserMetadataResponse>>

    fun setCurrentGoal(userId: String, goalId: String) : Maybe<Either<BaseError, UserMetadataResponse>>

    fun setCurrentMilestone(userId: String, previousMilestone: String?, milestoneId: String) : Maybe<Either<BaseError, UserMetadataResponse>>

    fun setCurrentHabit(userId: String, habitId: String) : Maybe<Either<BaseError, UserMetadataResponse>>
}