package dev.rostisla.nyt.data.repository

import dev.rostisla.nyt.data.api.NytStoriesApi
import dev.rostisla.nyt.data.database.StoryDao
import dev.rostisla.nyt.data.mapper.toBook
import dev.rostisla.nyt.data.mapper.toEntity
import dev.rostisla.nyt.data.mapper.toStoriesSectionDto
import dev.rostisla.nyt.data.mapper.toStory
import dev.rostisla.nyt.domain.model.Book
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
        return dao.getAllAsFlowBySection(section.name).map { entities ->
            entities.map { it.toStory() }
        }
    }

    override suspend fun fetchStories(section: StoriesSection) {
        val stories = if (section == StoriesSection.BOOKS) {
            val response = api.fetchBooksOverview()
            response.results.lists.flatMap { list ->
                list.books.map { book ->
                    Story(
                        title = book.title,
                        abstract = "[${list.displayName}] ${book.description ?: ""}",
                        publishedDate = "Author: ${book.author ?: "Unknown"}"
                    )
                }
            }
        } else {
            val response = api.fetchStories(section.toStoriesSectionDto())
            response.results.map { it.toStory() }
        }

        val entities = stories.map { it.toEntity(section) }
        
        dao.clearBySection(section.name)
        dao.insert(entities)
    }

    override suspend fun getBookList(listName: String): List<Book> {
        val response = api.fetchBookList(listName)
        return response.results.books.map { it.toBook() }
    }
}
