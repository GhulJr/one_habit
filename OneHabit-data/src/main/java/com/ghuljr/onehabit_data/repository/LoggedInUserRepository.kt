package com.ghuljr.onehabit_data.repository

import arrow.core.Either
import com.ghuljr.onehabit_error.BaseError
import com.ghuljr.onehabit_tools.base.network.LoggedInUserService
import com.ghuljr.onehabit_tools.base.network.RegisterRequest
import com.ghuljr.onehabit_tools.base.network.UserResponse
import com.ghuljr.onehabit_tools.di.ComputationScheduler
import com.ghuljr.onehabit_tools.di.NetworkScheduler
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoggedInUserRepository @Inject constructor(
    private val loggedInUserService: LoggedInUserService,
    @NetworkScheduler private val networkScheduler: Scheduler,
    @ComputationScheduler private  val computationScheduler: Scheduler
) : LoggedInUserService by loggedInUserService {

    //TODO(!!!): cache logged in user in the token storage

    fun register(registerRequest: RegisterRequest): Single<Either<BaseError, UserResponse>> = loggedInUserService
        .register(registerRequest.email, registerRequest.password)
        .subscribeOn(networkScheduler)

}