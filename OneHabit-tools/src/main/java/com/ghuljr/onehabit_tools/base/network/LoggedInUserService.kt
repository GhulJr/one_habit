package com.ghuljr.onehabit_tools.base.network

import arrow.core.Option
import io.reactivex.rxjava3.core.Flowable

interface LoggedInUserService {
    val userIdFlowable: Flowable<Option<String>>
    val isUserLoggedInFlowable: Flowable<Boolean>
    fun setToken(tokenOption: Option<String>): Boolean
}