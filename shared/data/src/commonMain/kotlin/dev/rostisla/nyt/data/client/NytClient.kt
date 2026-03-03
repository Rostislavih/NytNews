package dev.rostisla.nyt.data.client

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.parameters
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.appendIfNameAndValueAbsent
import kotlinx.serialization.json.Json

private const val API_KEY = "REDACTED_NYT_API_KEY"

val nytClient: () -> HttpClient = {
    HttpClient {
        expectSuccess = true
        install(Logging) {
            level = LogLevel.ALL
        }
        install(HttpTimeout) {
            val timeout = 5000L
            connectTimeoutMillis = timeout
            requestTimeoutMillis = timeout
            socketTimeoutMillis = timeout
        }
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                }
            )
        }
        defaultRequest {
            url("https://api.nytimes.com/svc/topstories/v2/")
            url.parameters.appendIfNameAndValueAbsent("api-key", API_KEY)
        }
    }
}
