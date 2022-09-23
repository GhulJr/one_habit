package com.ghuljr.onehabit_data.network.service

import arrow.core.Either
import com.ghuljr.onehabit_data.network.model.ActionResponse
import com.ghuljr.onehabit_error.BaseError
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single

interface ActionsService {

    fun getActionsFromGoal(goalId: String): Maybe<Either<BaseError, List<ActionResponse>>>
}