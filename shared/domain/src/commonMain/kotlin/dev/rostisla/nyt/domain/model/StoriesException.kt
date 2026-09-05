package dev.rostisla.nyt.domain.model

/**
 * Единственный тип ошибки, который слой данных отдаёт наружу.
 * [error] — причина для показа пользователю, [message] — техническая подробность.
 */
class StoriesException(
    val error: StoriesError,
    message: String? = null,
    cause: Throwable? = null,
) : Exception(message, cause)
