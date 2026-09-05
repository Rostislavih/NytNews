package dev.rostisla.nyt.presentation

import dev.rostisla.nyt.domain.model.Book
import dev.rostisla.nyt.domain.model.StoriesSection
import dev.rostisla.nyt.domain.model.Story
import dev.rostisla.nyt.domain.repository.StoriesRepository
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow

internal class FakeStoriesRepository : StoriesRepository {

    /** Что "лежит в базе" по каждому разделу. */
    private val cache = mutableMapOf<StoriesSection, MutableStateFlow<List<Story>>>()

    /** Чем закончится следующий fetchStories: null — успехом. */
    var fetchError: Throwable? = null

    /** Что fetchStories положит в кэш при успехе. */
    var fetchResult: List<Story> = emptyList()

    /** Если задано — getStories вместо данных упадёт с этой ошибкой. */
    var storageError: Throwable? = null

    /** Разделы, для которых запрашивалась сеть, в порядке вызовов. */
    val fetchedSections = mutableListOf<StoriesSection>()

    /** Если задано — fetchStories повиснет, пока тест не завершит этот Deferred. */
    var fetchGate: CompletableDeferred<Unit>? = null

    fun seed(section: StoriesSection, stories: List<Story>) {
        cacheOf(section).value = stories
    }

    override fun getStories(section: StoriesSection): Flow<List<Story>> {
        val error = storageError
        return if (error != null) flow { throw error } else cacheOf(section)
    }

    override suspend fun fetchStories(section: StoriesSection) {
        fetchedSections += section
        fetchGate?.await()
        fetchError?.let { throw it }
        cacheOf(section).value = fetchResult
    }

    override suspend fun getBookList(listName: String): List<Book> = emptyList()

    private fun cacheOf(section: StoriesSection): MutableStateFlow<List<Story>> =
        cache.getOrPut(section) { MutableStateFlow(emptyList()) }
}

internal fun story(title: String = "Title", url: String = "https://nyt.com/$title") = Story(
    url = url,
    title = title,
    abstract = "Abstract",
    publishedDate = "2026-09-05T10:00:00-04:00"
)
