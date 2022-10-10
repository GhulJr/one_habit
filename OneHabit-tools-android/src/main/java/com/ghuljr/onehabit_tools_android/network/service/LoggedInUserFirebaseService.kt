package com.ghuljr.onehabit_tools_android.network.service

import arrow.core.*
import com.ghuljr.onehabit_data.network.model.UserAuthResponse
import com.ghuljr.onehabit_error.BaseError
import com.ghuljr.onehabit_error.LoggedOutError
import com.ghuljr.onehabit_error_android.extension.orLoggedOutError
import com.ghuljr.onehabit_error_android.extension.resumeWithBaseError
import com.ghuljr.onehabit_data.network.service.LoggedInUserService
import com.ghuljr.onehabit_error.BaseEvent
import com.ghuljr.onehabit_error.RequireReAuthenticationEvent
import com.ghuljr.onehabit_error_android.extension.leftOnThrow
import com.ghuljr.onehabit_tools.di.ComputationScheduler
import com.ghuljr.onehabit_tools.di.NetworkScheduler
import com.ghuljr.onehabit_tools.extension.blankToOption
import com.ghuljr.onehabit_tools.extension.mapRight
import com.ghuljr.onehabit_tools.extension.mapRightWithEither
import com.ghuljr.onehabit_tools.extension.toRx3
import com.ghuljr.onehabit_tools_android.tool.asUnitSingle
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import io.ashdavies.rx.rxtasks.toSingle
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.processors.BehaviorProcessor
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoggedInUserFirebaseService @Inject constructor(
    @ComputationScheduler private val computationScheduler: Scheduler,
    @NetworkScheduler private val networkScheduler: Scheduler
) : LoggedInUserService {

    private val userProcessor = BehaviorProcessor.create<Option<UserAuthResponse>>()
    private val firebaseAuth: FirebaseAuth by lazy { Firebase.auth }
    private val onUserChangeListener = FirebaseAuth.AuthStateListener {
        userProcessor.onNext(it.currentUser?.toUserResponse().toOption())
    }

    private val singleThreadScheduler = Schedulers.from(Executors.newSingleThreadExecutor())

    //TODO: check if there might be some leaks. If yes, then hold this listener in App.kt
    init {
        firebaseAuth.removeAuthStateListener(onUserChangeListener)
        firebaseAuth.addAuthStateListener(onUserChangeListener)
    }

    override val isUserLoggedInFlowable: Flowable<Boolean> = userProcessor
        .map { it.isDefined() }
        .subscribeOn(computationScheduler)
        .replay(1).refCount()

    override val userFlowable: Flowable<Option<UserAuthResponse>> = userProcessor
        .subscribeOn(computationScheduler)
        .replay(1).refCount()

    override fun register(
        email: String,
        password: String
    ): Single<Either<BaseError, UserAuthResponse>> =
        firebaseAuth
            .createUserWithEmailAndPassword(email, password)
            .toSingle()
            .handleSignInOrLogOut()
            .resumeWithBaseError()
            .subscribeOn(networkScheduler)

    override fun signIn(
        email: String,
        password: String
    ): Single<Either<BaseError, UserAuthResponse>> =
        firebaseAuth
            .signInWithEmailAndPassword(email, password)
            .toSingle()
            .handleSignInOrLogOut()
            .resumeWithBaseError()
            .subscribeOn(networkScheduler)

    override fun changeUsername(displayName: String): Single<Either<BaseError, UserAuthResponse>> =
        firebaseAuth
            .currentUser?.updateProfile(userProfileChangeRequest {
                this.displayName = displayName
            })
            ?.asUnitSingle()
            .toOption()
            .getOrElse { Single.just(LoggedOutError.left()) }
            .updateSynchronouslyWithUser()
            .resumeWithBaseError()
            .subscribeOn(networkScheduler)

    override fun sendAuthorisationEmail(): Single<Either<BaseError, Unit>> =
        firebaseAuth.currentUser
            ?.sendEmailVerification()
            ?.asUnitSingle()
            .toOption()
            .map { Single.just(Unit.right() as Either<BaseError, Unit>) }
            .getOrElse { Single.just(LoggedOutError.left() as Either<BaseError, Unit>) }
            .resumeWithBaseError()
            .subscribeOn(networkScheduler)

    override fun refreshUser(): Single<Either<BaseError, UserAuthResponse>> = firebaseAuth
        .currentUser?.reload()
        ?.asUnitSingle()
        .toOption()
        .getOrElse { Single.just(LoggedOutError.left()) }
        .updateSynchronouslyWithUser()
        .resumeWithBaseError()
        .subscribeOn(networkScheduler)

    override fun resetPassword(email: String): Single<Either<BaseError, Unit>> =
        firebaseAuth.sendPasswordResetEmail(email)
            .asUnitSingle()
            .toOption().getOrElse { Single.just(LoggedOutError.left()) }
            .map { Unit.right() as Either<BaseError, Unit> }
            .resumeWithBaseError()
            .subscribeOn(networkScheduler)

    override fun signOut() {
        firebaseAuth.signOut()
    }

    override fun changeEmail(email: String): Single<Either<BaseEvent, UserAuthResponse>> =
        firebaseAuth.currentUser?.updateEmail(email)
            ?.asUnitSingle()
            ?.leftOnThrow()
            ?.flatMap { userFlowable.first(none()).map { it.toEither { LoggedOutError as BaseEvent } } }
            ?.mapRightWithEither { if (it.email != email) RequireReAuthenticationEvent.left() else it.right() }
            ?: Single.just(LoggedOutError.left())

    override fun reAuthenticate(
        email: String,
        password: String
    ): Single<Either<BaseError, UserAuthResponse>> = firebaseAuth
        .currentUser?.reauthenticate(EmailAuthProvider.getCredential(email, password))
        ?.asUnitSingle()
        ?.leftOnThrow()
        ?.flatMap { userFlowable.first(none()).map { it.toEither { LoggedOutError as BaseError } } }
        ?.subscribeOn(networkScheduler)
        ?: Single.just(LoggedOutError.left())

    private fun FirebaseUser.toUserResponse(): UserAuthResponse = UserAuthResponse(
        userId = uid,
        email = email!!,    // Right now we got only on auth method
        username = displayName.blankToOption(),
        isEmailVerified = isEmailVerified
    )

    private fun Single<*>.updateSynchronouslyWithUser(): Single<Either<BaseError, UserAuthResponse>> =
        map {
            firebaseAuth.currentUser?.toUserResponse()
                .rightIfNotNull { LoggedOutError } as Either<BaseError, UserAuthResponse>
        }
            .observeOn(singleThreadScheduler)
            .doOnSuccess { userProcessor.onNext(it.orNone()) }
            .observeOn(networkScheduler)

    private fun io.reactivex.Single<AuthResult>.handleSignInOrLogOut(): Single<Either<BaseError, UserAuthResponse>> =
        toRx3()
            .map {
                Either.catch { it.user!!.toUserResponse() }
                    .orLoggedOutError()
            }
}