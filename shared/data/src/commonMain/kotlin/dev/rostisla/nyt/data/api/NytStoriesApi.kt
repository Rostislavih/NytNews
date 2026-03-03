package dev.rostisla.nyt.data.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

internal interface NytStoriesApi {
    suspend fun fetchStories(section: StoriesSectionDto = StoriesSectionDto.HOME): NytStoriesDto
}

internal class NytStoriesApiImpl(private val client: HttpClient) : NytStoriesApi {
    override suspend fun fetchStories(section: StoriesSectionDto): NytStoriesDto {
        return client.get("${section.value.lowercase()}.json").body()
    }
}
