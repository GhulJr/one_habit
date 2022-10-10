package com.ghuljr.onehabit_error

// TODO: Extract base events and errors and make other exceptions inherit them
interface BaseEvent
object LoadingEvent : BaseEvent

interface BaseError : BaseEvent
object NoDataError: BaseError
object LoggedOutError: BaseError

data class GenericError(val cause: Throwable): BaseError


