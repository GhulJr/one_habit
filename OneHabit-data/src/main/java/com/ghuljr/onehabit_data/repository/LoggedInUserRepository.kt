package com.ghuljr.onehabit_data.repository

import arrow.core.*
import com.ghuljr.onehabit_data.base.storage.PropertyHolder
import com.ghuljr.onehabit_data.network.service.LoggedInUserManager
import com.ghuljr.onehabit_data.network.service.LoggedInUserService
import com.ghuljr.onehabit_error.AuthError
import com.ghuljr.onehabit_error.BaseError
import com.ghuljr.onehabit_tools.base.network.*
import com.ghuljr.onehabit_tools.di.ComputationScheduler
import com.ghuljr.onehabit_tools.di.NetworkScheduler
import com.ghuljr.onehabit_tools.extension.*
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoggedInUserRepository @Inject constructor(
    private val loggedInUserService: LoggedInUserService,
    @NetworkScheduler private val networkScheduler: Scheduler,
    @ComputationScheduler private val computationScheduler: Scheduler,
    private val propertyHolderFactory: PropertyHolder.Factory<Boolean>
) : LoggedInUserManager by loggedInUserService {

    private val propertyHolder: PropertyHolder<Boolean> =
        propertyHolderFactory.create(KEY_IS_EMAIL_VERIFICATION_SEND, none())

    val isEmailVerificationSendFlowable: Flowable<Boolean> = propertyHolder.get()
        .map { it.getOrElse { false } }
        .switchMap { isSend ->
            userFlowable.map {
                it.map { it.isEmailVerified }.getOrElse { false } || isSend
            }
        }
        .subscribeOn(computationScheduler)
        .replay(1)
        .refCount()

    val userIdFlowable: Flowable<Option<String>> = userFlowable
        .map { it.map { it.userId } }
        .subscribeOn(computationScheduler)
        .replay(1)
        .refCount()

    fun register(registerRequest: RegisterRequest): Single<Either<BaseError, UserResponse>> =
        loggedInUserService
            .register(registerRequest.email, registerRequest.password)
            .subscribeOn(networkScheduler)

    fun signIn(loginRequest: LoginRequest): Single<Either<BaseError, UserResponse>> =
        loggedInUserService
            .signIn(loginRequest.email, loginRequest.password)
            .subscribeOn(networkScheduler)

    fun sendEmailVerification(): Single<Either<BaseError, Boolean>> = loggedInUserService
        .sendAuthorisationEmail()
        .subscribeOn(networkScheduler)
        .flatMapRight { propertyHolder.set(true.some()) }

    fun refreshUser(): Single<Either<BaseError, UserResponse>> = loggedInUserService
        .refreshUser()
        .mapRightWithEither {
            if (!it.isEmailVerified) AuthError.EmailNotYetVerified.left()
            else it.right()
        }

    companion object {
        const val KEY_IS_EMAIL_VERIFICATION_SEND = "is_email_verification_send"
    }
}