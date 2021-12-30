package com.ghuljr.onehabit_tools.base.network

import arrow.core.Either
import arrow.core.Option
import com.ghuljr.onehabit_error.BaseError
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single

interface LoggedInUserManager {
    val userFlowable: Flowable<Option<UserResponse>>
    val isUserLoggedInFlowable: Flowable<Boolean>
    fun changeDisplayName(displayName: String): Single<Either<BaseError, UserResponse>>
    fun signOut()
}

interface LoggedInUserService: LoggedInUserManager {
    fun register(email: String, password: String): Single<Either<BaseError, UserResponse>>
    fun signIn(email: String, password: String): Single<Either<BaseError, UserResponse>>
    fun sendAuthorisationEmail(): Single<Either<BaseError, Unit>>
    fun refreshUser(): Single<Either<BaseError, UserResponse>>
    fun resetPassword(email: String): Single<Either<BaseError, Unit>>
}

data class RegisterRequest(val email: String, val password: String)
data class LoginRequest(val email: String, val password: String)

data class UserResponse(
    val userId: String,
    val email: String,
    val username: Option<String>,
    val isEmailVerified: Boolean
)