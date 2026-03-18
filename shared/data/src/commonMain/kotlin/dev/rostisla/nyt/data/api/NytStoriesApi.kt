package dev.rostisla.nyt.data.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

internal interface NytStoriesApi {
    suspend fun fetchStories(section: StoriesSectionDto = StoriesSectionDto.HOME): NytStoriesDto
    suspend fun fetchBooksOverview(): NytOverviewResponseDto
    suspend fun fetchBookList(list: String, date: String = "current"): NytListResponseDto
}

internal class NytStoriesApiImpl(private val client: HttpClient) : NytStoriesApi {
    override suspend fun fetchStories(section: StoriesSectionDto): NytStoriesDto {
        // Этот метод только для Top Stories (v2)
        return client.get("${section.value.lowercase()}.json").body()
    }

    override suspend fun fetchBooksOverview(): NytOverviewResponseDto {
        return client.get("books/v3/lists/overview.json").body()
    }

    override suspend fun fetchBookList(list: String, date: String): NytListResponseDto {
        return client.get("books/v3/lists/$date/$list.json").body()
    }
}
