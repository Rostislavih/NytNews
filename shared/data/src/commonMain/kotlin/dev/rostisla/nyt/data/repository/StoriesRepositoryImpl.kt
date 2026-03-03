package dev.rostisla.nyt.data.repository

import dev.rostisla.nyt.data.api.NytStoriesApi
import dev.rostisla.nyt.data.mapper.toStoriesSectionDto
import dev.rostisla.nyt.data.mapper.toStory
import dev.rostisla.nyt.domain.model.StoriesSection
import dev.rostisla.nyt.domain.model.Story
import dev.rostisla.nyt.domain.repository.StoriesRepository

internal class StoriesRepositoryImpl(private val api: NytStoriesApi) : StoriesRepository {

    override suspend fun fetchStories(section: StoriesSection): List<Story> {
        return api.fetchStories(section.toStoriesSectionDto()).results.map { it.toStory() }
    }
}