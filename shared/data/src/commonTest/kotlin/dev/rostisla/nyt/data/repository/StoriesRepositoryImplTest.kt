package dev.rostisla.nyt.data.repository

import dev.rostisla.nyt.data.api.NytBookDto
import dev.rostisla.nyt.data.api.NytListResponseDto
import dev.rostisla.nyt.data.api.NytListResultDto
import dev.rostisla.nyt.data.api.NytOverviewListDto
import dev.rostisla.nyt.data.api.NytOverviewResponseDto
import dev.rostisla.nyt.data.api.NytOverviewResultsDto
import dev.rostisla.nyt.data.api.NytStoriesApi
import dev.rostisla.nyt.data.api.NytStoriesDto
import dev.rostisla.nyt.data.api.NytStoryDto
import dev.rostisla.nyt.data.api.StoriesSectionDto
import dev.rostisla.nyt.data.database.StoryDao
import dev.rostisla.nyt.data.database.StoryEntity
import dev.rostisla.nyt.domain.model.StoriesError
import dev.rostisla.nyt.domain.model.StoriesException
import dev.rostisla.nyt.domain.model.StoriesSection
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import kotlinx.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

internal class StoriesRepositoryImplTest {

    private val api = FakeNytStoriesApi()
    private val dao = FakeStoryDao()

    @Test
    fun `blank api key fails before any request goes out`() = runTest {
        val repository = StoriesRepositoryImpl(api, dao, apiKey = "")

        val error = assertFailsWith<StoriesException> {
            repository.fetchStories(StoriesSection.HOME)
        }

        assertEquals(StoriesError.API_KEY_MISSING, error.error)
        assertTrue(api.requestedSections.isEmpty(), "запрос не должен уходить без ключа")
    }

    @Test
    fun `network failure is wrapped into StoriesException with a mapped reason`() = runTest {
        api.error = UnresolvedAddressException()
        val repository = repository()

        val error = assertFailsWith<StoriesException> {
            repository.fetchStories(StoriesSection.HOME)
        }

        assertEquals(StoriesError.NO_INTERNET, error.error)
    }

    @Test
    fun `database failure on write is reported as STORAGE`() = runTest {
        api.stories = listOf(storyDto("First"))
        dao.insertError = IOException("disk full")
        val repository = repository()

        val error = assertFailsWith<StoriesException> {
            repository.fetchStories(StoriesSection.HOME)
        }

        assertEquals(StoriesError.STORAGE, error.error)
    }

    @Test
    fun `database failure on read is reported as STORAGE`() = runTest {
        dao.readError = IOException("database is locked")
        val repository = repository()

        val error = assertFailsWith<StoriesException> {
            repository.getStories(StoriesSection.HOME).first()
        }

        assertEquals(StoriesError.STORAGE, error.error)
    }

    @Test
    fun `fetched stories are stored under the requested section`() = runTest {
        api.stories = listOf(storyDto("First"), storyDto("Second"))
        val repository = repository()

        repository.fetchStories(StoriesSection.ARTS)

        assertEquals(listOf(StoriesSectionDto.ARTS), api.requestedSections)
        assertEquals(listOf("First", "Second"), dao.inserted.map { it.title })
        assertTrue(dao.inserted.all { it.section == StoriesSection.ARTS.name })
    }

    @Test
    fun `stored stories are read back as domain models`() = runTest {
        val repository = repository()
        api.stories = listOf(storyDto("First"))
        repository.fetchStories(StoriesSection.HOME)

        val stories = repository.getStories(StoriesSection.HOME).first()

        assertEquals(listOf("First"), stories.map { it.title })
    }

    @Test
    fun `books section goes to the overview endpoint`() = runTest {
        api.books = listOf("Dune" to "Frank Herbert")
        val repository = repository()

        repository.fetchStories(StoriesSection.BOOKS)

        assertTrue(api.overviewRequested)
        assertTrue(api.requestedSections.isEmpty(), "разделу BOOKS не нужен top stories endpoint")
        assertEquals(listOf("Dune"), dao.inserted.map { it.title })
    }

    private fun repository() = StoriesRepositoryImpl(api, dao, apiKey = "test-key")

    private fun storyDto(title: String) = NytStoryDto(
        url = "https://nyt.com/$title",
        title = title,
        abstract = "Abstract",
        publishedDate = "2026-09-05T10:00:00-04:00"
    )
}

private class FakeNytStoriesApi : NytStoriesApi {

    var error: Throwable? = null
    var stories: List<NytStoryDto> = emptyList()
    var books: List<Pair<String, String>> = emptyList()

    val requestedSections = mutableListOf<StoriesSectionDto>()
    var overviewRequested = false

    override suspend fun fetchStories(section: StoriesSectionDto): NytStoriesDto {
        error?.let { throw it }
        requestedSections += section
        return NytStoriesDto(results = stories)
    }

    override suspend fun fetchBooksOverview(): NytOverviewResponseDto {
        error?.let { throw it }
        overviewRequested = true
        return NytOverviewResponseDto(
            status = "OK",
            results = NytOverviewResultsDto(
                publishedDate = "2026-09-05",
                lists = listOf(
                    NytOverviewListDto(
                        displayName = "Fiction",
                        listNameEncoded = "fiction",
                        updated = "WEEKLY",
                        books = books.mapIndexed { index, (title, author) ->
                            NytBookDto(
                                rank = index + 1,
                                title = title,
                                author = author,
                                description = "Description"
                            )
                        }
                    )
                )
            )
        )
    }

    override suspend fun fetchBookList(list: String, date: String): NytListResponseDto {
        error?.let { throw it }
        return NytListResponseDto(
            status = "OK",
            results = NytListResultDto(
                displayName = list,
                listNameEncoded = list,
                publishedDate = date,
                updated = "WEEKLY",
                books = emptyList()
            )
        )
    }
}

private class FakeStoryDao : StoryDao {

    var insertError: Throwable? = null
    var readError: Throwable? = null

    val inserted = mutableListOf<StoryEntity>()
    private val rows = MutableStateFlow<List<StoryEntity>>(emptyList())

    override suspend fun insert(story: List<StoryEntity>) {
        insertError?.let { throw it }
        inserted += story
        rows.value = story
    }

    override fun getAllAsFlowBySection(section: String): Flow<List<StoryEntity>> {
        val error = readError
        return if (error != null) flow { throw error } else rows
    }

    override suspend fun getAllBySection(section: String): List<StoryEntity> = rows.value

    override suspend fun clearAll() {
        rows.value = emptyList()
    }
}
