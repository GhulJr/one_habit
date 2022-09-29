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
        actionRequest: ActionRequest,
        goalId: String,
    ): Maybe<Either<BaseError, ActionResponse>>

    fun putActions(
        actionRequests: List<Pair<String, ActionRequest>>
    ): Maybe<Either<BaseError, List<ActionResponse>>>

    fun putOneActionToManyGoals(
        actionRequests: ActionRequest,
        goalIds: List<String>
    ): Maybe<Either<BaseError, ActionResponse>>

    fun editCustomAction(
        actionName: String,
        userId: String,
        actionId: String,
        reminders: List<Long>
    ): Maybe<Either<BaseError, ActionResponse>>

    fun addRemindersToAction(
        userId: String,
        actionId: String,
        reminders: List<Long>
    ): Maybe<Either<BaseError, ActionResponse>>

    fun removeAction(
        actionId: String,
        userId: String,
        goalId: String
    ): Maybe<Either<BaseError, Unit>>
}