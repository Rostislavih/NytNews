package dev.rostisla.nyt.data.client

import dev.rostisla.nyt.data.NytConfig
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

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
            url.parameters.append("api-key", NytConfig.API_KEY)
        }
    }
}
