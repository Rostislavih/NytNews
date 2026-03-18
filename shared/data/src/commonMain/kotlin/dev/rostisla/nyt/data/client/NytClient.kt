package dev.rostisla.nyt.data.client

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

// Используем полный ключ, который был в вашем первом сообщении.
// Если ошибка "Invalid ApiKey for given resource" сохраняется, 
// проверьте, что в кабинете разработчика NYT для этого ключа включен "Books API".
private const val API_KEY = "REDACTED_NYT_API_KEY"

val nytClient: () -> HttpClient = {
    HttpClient {
        expectSuccess = true
        install(Logging) {
            level = LogLevel.ALL
        }
        install(HttpTimeout) {
            val timeout = 15000L
            connectTimeoutMillis = timeout
            requestTimeoutMillis = timeout
            socketTimeoutMillis = timeout
        }
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    coerceInputValues = true
                }
            )
        }
        defaultRequest {
            url("https://api.nytimes.com/svc/")
            url.parameters.append("api-key", API_KEY)
        }
    }
}
