package com.ghuljr.onehabit_error

interface BaseEvent

object LoadingEvent : BaseEvent
object LoggedOutEvent: BaseEvent

interface BaseError : BaseEvent
object NoDataError: BaseError
