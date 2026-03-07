package dev.rostisla.nyt.domain.repository

import dev.rostisla.nyt.domain.model.StoriesSection
import dev.rostisla.nyt.domain.model.Story
import kotlinx.coroutines.flow.Flow

interface StoriesRepository {
    /**
     * Возвращает поток данных из локальной базы данных.
     * UI будет автоматически обновляться при изменении данных в БД.
     */
    fun getStories(section: StoriesSection): Flow<List<Story>>

    /**
     * Загружает свежие данные из сети и сохраняет их в базу.
     */
    suspend fun fetchStories(section: StoriesSection)
}
