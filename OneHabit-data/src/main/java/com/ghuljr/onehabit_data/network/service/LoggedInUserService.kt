package com.ghuljr.onehabit_data.network.service

import arrow.core.Either
import arrow.core.Option
import com.ghuljr.onehabit_data.network.model.UserResponse
import com.ghuljr.onehabit_error.BaseError
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single

interface LoggedInUserManager {
    val userFlowable: Flowable<Option<UserResponse>>
    val isUserLoggedInFlowable: Flowable<Boolean>
    fun changeUsername(displayName: String): Single<Either<BaseError, UserResponse>>
    fun resetPassword(email: String): Single<Either<BaseError, Unit>>
    fun signOut()
}

interface LoggedInUserService: LoggedInUserManager {
    fun register(email: String, password: String): Single<Either<BaseError, UserResponse>>
    fun signIn(email: String, password: String): Single<Either<BaseError, UserResponse>>
    fun sendAuthorisationEmail(): Single<Either<BaseError, Unit>>
    fun refreshUser(): Single<Either<BaseError, UserResponse>>
}