package dev.rostisla.nyt.data.mapper

import dev.rostisla.nyt.domain.model.StoriesError
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.serialization.JsonConvertException
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException

internal fun Throwable.toStoriesError(): StoriesError = when (this) {
    is UnresolvedAddressException -> StoriesError.NO_INTERNET

    is HttpRequestTimeoutException,
    is ConnectTimeoutException,
    is SocketTimeoutException -> StoriesError.TIMEOUT

    is ClientRequestException -> when (response.status.value) {
        401, 403 -> StoriesError.UNAUTHORIZED
        404 -> StoriesError.NOT_FOUND
        429 -> StoriesError.RATE_LIMITED
        else -> StoriesError.UNKNOWN
    }

    is ServerResponseException -> StoriesError.SERVER

    is JsonConvertException,
    is SerializationException -> StoriesError.BAD_RESPONSE

    is IOException -> StoriesError.NO_INTERNET

    else -> StoriesError.UNKNOWN
}
