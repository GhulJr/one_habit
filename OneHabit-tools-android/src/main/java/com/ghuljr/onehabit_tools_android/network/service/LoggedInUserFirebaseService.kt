package com.ghuljr.onehabit_tools_android.network.service

import arrow.core.*
import com.ghuljr.onehabit_error.BaseError
import com.ghuljr.onehabit_error.LoggedOutError
import com.ghuljr.onehabit_error_android.extension.orLoggedOutError
import com.ghuljr.onehabit_error_android.extension.resumeWithBaseError
import com.ghuljr.onehabit_tools.base.network.LoggedInUserService
import com.ghuljr.onehabit_tools.base.network.UserResponse
import com.ghuljr.onehabit_tools.di.ComputationScheduler
import com.ghuljr.onehabit_tools.di.NetworkScheduler
import com.ghuljr.onehabit_tools.extension.toRx3
import com.google.firebase.auth.AuthResult
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
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoggedInUserFirebaseService @Inject constructor(
    @ComputationScheduler private val computationScheduler: Scheduler,
    @NetworkScheduler private val networkScheduler: Scheduler
) : LoggedInUserService {

    private val userProcessor = BehaviorProcessor.create<Option<UserResponse>>()
    private val firebaseAuth: FirebaseAuth by lazy { Firebase.auth }
    private val onUserChangeListener = FirebaseAuth.AuthStateListener {
        userProcessor.onNext(it.currentUser?.toUserResponse().toOption())
    }

    //TODO: check if there might be some leaks. If yes, then hold this listener in App.kt
    init {
        firebaseAuth.removeAuthStateListener(onUserChangeListener)
        firebaseAuth.addAuthStateListener(onUserChangeListener)
    }

    override val isUserLoggedInFlowable: Flowable<Boolean> = userProcessor
        .map { it.isDefined() }
        .subscribeOn(computationScheduler)
        .replay(1).refCount()

    override val userFlowable: Flowable<Option<UserResponse>> = userProcessor
        .subscribeOn(computationScheduler)
        .replay(1).refCount()

    override fun register(
        email: String,
        password: String
    ): Single<Either<BaseError, UserResponse>> =
        firebaseAuth
            .createUserWithEmailAndPassword(email, password)
            .toSingle()
            .handleSignInOrLogOut()
            .resumeWithBaseError()
            .subscribeOn(networkScheduler)

    override fun signIn(email: String, password: String): Single<Either<BaseError, UserResponse>> =
        firebaseAuth
            .signInWithEmailAndPassword(email, password)
            .toSingle()
            .handleSignInOrLogOut()
            .resumeWithBaseError()
            .subscribeOn(networkScheduler)

    override fun changeDisplayName(displayName: String): Single<Either<BaseError, Unit>> =
        firebaseAuth
            .currentUser?.updateProfile(userProfileChangeRequest {
                this.displayName = displayName
            })
            ?.toSingle()
            ?.toRx3()
            .toOption().getOrElse { Single.just(LoggedOutError.left()) }
            .map { Unit.right() as Either<BaseError, Unit> }
            .resumeWithBaseError()
            .subscribeOn(networkScheduler)

    override fun sendAuthorisationEmail(): Single<Either<BaseError, Unit>> = firebaseAuth
        .currentUser?.sendEmailVerification()
        ?.toSingle()
        ?.toRx3()
        .toOption().getOrElse { Single.just(LoggedOutError.left()) }
        .map { Unit.right() as Either<BaseError, Unit> }
        .resumeWithBaseError()
        .subscribeOn(networkScheduler)

    override fun refreshUser(): Single<Either<BaseError, Unit>> = firebaseAuth
        .currentUser?.reload()
        ?.toSingle()
        ?.toRx3()
        .toOption().getOrElse { Single.just(LoggedOutError.left()) }
        .map { Unit.right() as Either<BaseError, Unit> }
        .resumeWithBaseError()
        .subscribeOn(networkScheduler)

    //TODO: auth email
    override fun signOut() {
        firebaseAuth.signOut()
    }

    private fun FirebaseUser.toUserResponse(): UserResponse = UserResponse(
        userId = uid,
        email = email!!,    // Right now we got only on auth method
        username = displayName.toOption()
    )

    private fun io.reactivex.Single<AuthResult>.handleSignInOrLogOut(): Single<Either<BaseError, UserResponse>> =
        toRx3()
            .map {
                Either.catch { it.user!!.toUserResponse() }
                    .orLoggedOutError()
            }
}