package com.ghuljr.onehabit_tools_android.network.service

import arrow.core.Option
import arrow.core.toOption
import com.ghuljr.onehabit_data.model.network.UserResponse
import com.ghuljr.onehabit_tools.base.network.LoggedInUserService
import com.ghuljr.onehabit_tools.di.ComputationScheduler
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.processors.BehaviorProcessor
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoggedInUserFirebaseService @Inject constructor(
    @ComputationScheduler computationScheduler: Scheduler
): LoggedInUserService {

    private val userIdProcessor = BehaviorProcessor.create<Option<UserResponse>>()
    private val firebaseAuth: FirebaseAuth by lazy { Firebase.auth }
    private val onUserChangeListener = FirebaseAuth.AuthStateListener {
        userIdProcessor.onNext(it.currentUser?.toUserResponse().toOption())
    }

    //TODO: check if there might be some leaks. If yes, then hold this listener in App.kt
    init {
        firebaseAuth.removeAuthStateListener(onUserChangeListener)
        firebaseAuth.addAuthStateListener(onUserChangeListener)
    }

    override val isUserLoggedInFlowable: Flowable<Boolean> = userIdProcessor
        .map { it.isDefined() }
        .subscribeOn(computationScheduler)
        .replay(1).refCount()

    override val userIdFlowable: Flowable<Option<String>> = userIdProcessor
        .map { it.map { it.userId } }
        .subscribeOn(computationScheduler)
        .replay(1).refCount()


    override fun setToken(tokenOption: Option<String>): Boolean = true //TODO: use it as a login


    private fun FirebaseUser.toUserResponse(): UserResponse = UserResponse(
        userId = uid,
        email = email!!,    // Right now we got only on auth method
        username =  displayName.toOption()
    )
}