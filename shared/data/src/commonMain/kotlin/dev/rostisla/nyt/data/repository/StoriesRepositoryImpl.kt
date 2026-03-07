package dev.rostisla.nyt.data.repository

import dev.rostisla.nyt.data.api.NytStoriesApi
import dev.rostisla.nyt.data.database.StoryDao
import dev.rostisla.nyt.data.mapper.toEntity
import dev.rostisla.nyt.data.mapper.toStoriesSectionDto
import dev.rostisla.nyt.data.mapper.toStory
import dev.rostisla.nyt.domain.model.StoriesSection
import dev.rostisla.nyt.domain.model.Story
import dev.rostisla.nyt.domain.repository.StoriesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class StoriesRepositoryImpl(
    private val api: NytStoriesApi,
    private val dao: StoryDao,
) : StoriesRepository {

    override fun getStories(section: StoriesSection): Flow<List<Story>> {
        // Мы возвращаем поток данных из DAO, преобразуя Entity в доменные модели Story.
        // Это позволяет UI реагировать на любые изменения в базе данных.
        return dao.getAllAsFlow().map { entities ->
            entities.map { it.toStory() }
        }
    }

    override suspend fun fetchStories(section: StoriesSection) {
        // 1. Загружаем свежие данные из API
        val response = api.fetchStories(section.toStoriesSectionDto())
        
        // 2. Преобразуем ответ API в сущности БД (Entity)
        val entities = response.results.map { it.toStory().toEntity(section) }
        
        // 3. Сохраняем в базу данных. 
        // В реальном приложении здесь можно было бы сначала очистить старые данные (dao.clear()).
        dao.insert(entities)
    }
}
