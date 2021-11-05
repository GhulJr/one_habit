package com.ghuljr.onehabit_error

sealed class BaseEvent

object LoadingEvent : BaseEvent()
object LoggedOutEvent: BaseEvent()
object NoDataEvent: BaseEvent()

sealed class BaseError : BaseEvent()
