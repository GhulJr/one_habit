package com.ghuljr.onehabit_error

sealed class NetworkError(open val message: String?): BaseError {
    data class NoNetwork(override val message: String?): NetworkError(message)
    data class ServerUnavailable(override val message: String?): NetworkError(message)
    data class TooManyRequests(override val message: String?): NetworkError(message)
}