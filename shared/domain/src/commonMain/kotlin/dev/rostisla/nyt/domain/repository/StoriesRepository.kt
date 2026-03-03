package dev.rostisla.nyt.domain.repository

import dev.rostisla.nyt.domain.model.StoriesSection
import dev.rostisla.nyt.domain.model.Story

interface StoriesRepository {
    suspend fun fetchStories(section: StoriesSection): List<Story>
}
