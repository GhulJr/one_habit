package com.ghuljr.onehabit_data.network.service

import arrow.core.Either
import arrow.core.Option
import com.ghuljr.onehabit_data.network.model.UserAuthResponse
import com.ghuljr.onehabit_error.BaseError
import com.ghuljr.onehabit_error.BaseEvent
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single

interface LoggedInUserManager {
    val userFlowable: Flowable<Option<UserAuthResponse>>
    val isUserLoggedInFlowable: Flowable<Boolean>
    fun changeUsername(displayName: String): Single<Either<BaseError, UserAuthResponse>>
    fun resetPassword(email: String): Single<Either<BaseError, Unit>>
    fun signOut()
}

interface LoggedInUserService: LoggedInUserManager {
    fun register(email: String, password: String): Single<Either<BaseError, UserAuthResponse>>
    fun signIn(email: String, password: String): Single<Either<BaseError, UserAuthResponse>>
    fun sendAuthorisationEmail(): Single<Either<BaseError, Unit>>
    fun refreshUser(): Single<Either<BaseError, UserAuthResponse>>
    fun changeEmail(email: String): Single<Either<BaseEvent, UserAuthResponse>>
    fun reAuthenticate(email: String, password: String) : Single<Either<BaseError, UserAuthResponse>>
}