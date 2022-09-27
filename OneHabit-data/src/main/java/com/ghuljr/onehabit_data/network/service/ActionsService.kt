package com.ghuljr.onehabit_data.network.service

import arrow.core.Either
import com.ghuljr.onehabit_data.network.model.ActionRequest
import com.ghuljr.onehabit_data.network.model.ActionResponse
import com.ghuljr.onehabit_error.BaseError
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single

interface ActionsService {

    fun getActionsFromGoal(
        goalId: String,
        userId: String
    ): Maybe<Either<BaseError, List<ActionResponse>>>

    fun completeActionStep(
        actionId: String,
        userId: String
    ): Maybe<Either<BaseError, ActionResponse>>

    fun revertCompleteActionStep(
        actionId: String,
        userId: String
    ): Maybe<Either<BaseError, ActionResponse>>

    fun putAction(
        actionRequest: ActionRequest
    ): Maybe<Either<BaseError, ActionResponse>>

    fun editAction(
        actionName: String,
        userId: String,
        goalId: String,
        actionId: String
    ): Maybe<Either<BaseError, ActionResponse>>

    fun removeAction(
        actionId: String,
        userId: String,
        goalId: String
    ): Maybe<Either<BaseError, Unit>>
}