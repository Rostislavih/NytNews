package dev.rostisla.nyt.data.mapper

import dev.rostisla.nyt.domain.model.StoriesError
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondError
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.test.runTest
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

internal class ThrowableToStoriesErrorMapperTest {

    @Test
    fun `no network is reported as NO_INTERNET`() {
        assertEquals(StoriesError.NO_INTERNET, UnresolvedAddressException().toStoriesError())
        assertEquals(StoriesError.NO_INTERNET, IOException("connection reset").toStoriesError())
    }

    @Test
    fun `every kind of timeout is reported as TIMEOUT`() {
        assertEquals(
            StoriesError.TIMEOUT,
            HttpRequestTimeoutException("https://api.nytimes.com", 15_000L).toStoriesError()
        )
        assertEquals(StoriesError.TIMEOUT, ConnectTimeoutException("connect timed out").toStoriesError())
        assertEquals(StoriesError.TIMEOUT, SocketTimeoutException("socket timed out").toStoriesError())
    }

    @Test
    fun `timeouts win over the IOException they inherit from`() {
        val timeout: Throwable = SocketTimeoutException("socket timed out")

        // Порядок веток в when важен: SocketTimeoutException — это тоже IOException.
        assertEquals(StoriesError.TIMEOUT, timeout.toStoriesError())
    }

    @Test
    fun `broken payload is reported as BAD_RESPONSE`() {
        assertEquals(StoriesError.BAD_RESPONSE, SerializationException("unexpected token").toStoriesError())
    }

    @Test
    fun `anything else is reported as UNKNOWN`() {
        assertEquals(StoriesError.UNKNOWN, IllegalStateException("boom").toStoriesError())
    }

    @Test
    fun `rejected api key is reported as UNAUTHORIZED`() = runTest {
        assertEquals(StoriesError.UNAUTHORIZED, errorFor(HttpStatusCode.Unauthorized))
        assertEquals(StoriesError.UNAUTHORIZED, errorFor(HttpStatusCode.Forbidden))
    }

    @Test
    fun `missing endpoint is reported as NOT_FOUND`() = runTest {
        assertEquals(StoriesError.NOT_FOUND, errorFor(HttpStatusCode.NotFound))
    }

    @Test
    fun `throttling is reported as RATE_LIMITED`() = runTest {
        assertEquals(StoriesError.RATE_LIMITED, errorFor(HttpStatusCode.TooManyRequests))
    }

    @Test
    fun `server side failure is reported as SERVER`() = runTest {
        assertEquals(StoriesError.SERVER, errorFor(HttpStatusCode.InternalServerError))
        assertEquals(StoriesError.SERVER, errorFor(HttpStatusCode.BadGateway))
    }

    @Test
    fun `unmapped 4xx stays UNKNOWN`() = runTest {
        assertEquals(StoriesError.UNKNOWN, errorFor(HttpStatusCode.PaymentRequired))
    }

    /** Гоняет настоящий Ktor-клиент, чтобы маппинг проверялся на реальных исключениях плагина. */
    private suspend fun errorFor(status: HttpStatusCode): StoriesError {
        val client = HttpClient(MockEngine { respondError(status) }) { expectSuccess = true }
        val thrown: Throwable? = try {
            client.get("https://api.nytimes.com/svc/topstories/v2/home.json").bodyAsText()
            null
        } catch (error: Exception) {
            error
        } finally {
            client.close()
        }
        return assertNotNull(thrown, "клиент должен упасть на статусе $status").toStoriesError()
    }

    @Test
    fun `successful response does not throw at all`() = runTest {
        val client = HttpClient(MockEngine { respond("{}") }) { expectSuccess = true }

        val body = client.get("https://api.nytimes.com/svc/topstories/v2/home.json").bodyAsText()

        assertEquals("{}", body)
        client.close()
    }
}
