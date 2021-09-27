package com.ghuljr.onehabit_error

sealed class BaseEvent

object LoadingEvent : BaseEvent()

sealed class BaseError : BaseEvent()
object LoggedOutError: BaseError()
