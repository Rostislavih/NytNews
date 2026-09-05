package dev.rostisla.nyt.data.repository

import dev.rostisla.nyt.data.api.NytStoriesApi
import dev.rostisla.nyt.data.database.StoryDao
import dev.rostisla.nyt.data.mapper.toBook
import dev.rostisla.nyt.data.mapper.toEntity
import dev.rostisla.nyt.data.mapper.toStoriesError
import dev.rostisla.nyt.data.mapper.toStoriesSectionDto
import dev.rostisla.nyt.data.mapper.toStory
import dev.rostisla.nyt.domain.model.Book
import dev.rostisla.nyt.domain.model.StoriesError
import dev.rostisla.nyt.domain.model.StoriesException
import dev.rostisla.nyt.domain.model.StoriesSection
import dev.rostisla.nyt.domain.model.Story
import dev.rostisla.nyt.domain.repository.StoriesRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

internal class StoriesRepositoryImpl(
    private val api: NytStoriesApi,
    private val dao: StoryDao,
    private val apiKey: String,
) : StoriesRepository {

    override fun getStories(section: StoriesSection): Flow<List<Story>> {
        return dao.getAllAsFlowBySection(section.name)
            .map { entities -> entities.map { it.toStory() } }
            .catch { error -> throw StoriesException(StoriesError.STORAGE, error.message, error) }
    }

    override suspend fun fetchStories(section: StoriesSection) {
        requireApiKey()

        val stories = network {
            if (section == StoriesSection.BOOKS) {
                val response = api.fetchBooksOverview()
                response.results.lists.flatMap { list ->
                    list.books.map { book ->
                        Story(
                            url = "book_${book.title}_${book.author}",
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
        }

        val entities = stories.map { it.toEntity(section) }

        storage { dao.insert(entities) }
    }

    override suspend fun getBookList(listName: String): List<Book> {
        requireApiKey()
        return network { api.fetchBookList(listName).results.books.map { it.toBook() } }
    }

    private fun requireApiKey() {
        if (apiKey.isBlank()) {
            throw StoriesException(
                error = StoriesError.API_KEY_MISSING,
                message = "nyt.api.key is missing in local.properties"
            )
        }
    }

    private inline fun <T> network(block: () -> T): T = try {
        block()
    } catch (error: CancellationException) {
        throw error
    } catch (error: Exception) {
        throw StoriesException(error.toStoriesError(), error.message, error)
    }

    private inline fun <T> storage(block: () -> T): T = try {
        block()
    } catch (error: CancellationException) {
        throw error
    } catch (error: Exception) {
        throw StoriesException(StoriesError.STORAGE, error.message, error)
    }
}
