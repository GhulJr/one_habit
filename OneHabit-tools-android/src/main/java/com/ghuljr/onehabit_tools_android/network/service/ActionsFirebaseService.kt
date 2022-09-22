package com.ghuljr.onehabit_tools_android.network.service

import com.ghuljr.onehabit_tools.base.network.ActionResponse
import com.ghuljr.onehabit_data.network.service.ActionsService
import com.ghuljr.onehabit_tools.di.ComputationScheduler
import com.ghuljr.onehabit_tools.di.NetworkScheduler
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import io.reactivex.rxjava3.core.Scheduler
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActionsFirebaseService @Inject constructor(
    @ComputationScheduler private val computationScheduler: Scheduler,
    @NetworkScheduler private val networkScheduler: Scheduler
) : ActionsService {

    // TODO: parametrise by user id
    // TODO: parametrise by sprint id - before proceeding further, design all the elements of the database (From Habit to Action)
    private val database = Firebase.database.getReference("actions")

    override fun getTodayActions(): List<ActionResponse> {

    }
}