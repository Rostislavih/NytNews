package dev.rostisla.nyt.presentation

import dev.rostisla.nyt.domain.model.StoriesError
import dev.rostisla.nyt.domain.model.StoriesException
import dev.rostisla.nyt.domain.model.StoriesSection
import dev.rostisla.nyt.presentation.state.StoriesState
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
internal class StoriesViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val repository = FakeStoriesRepository()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `state is Loading until the first fetch completes`() = runTest(dispatcher) {
        val viewModel = StoriesViewModel(repository)

        viewModel.onStart()

        assertEquals(StoriesState.Loading, viewModel.state.value)
    }

    @Test
    fun `loaded stories produce Success`() = runTest(dispatcher) {
        repository.fetchResult = listOf(story("First"), story("Second"))
        val viewModel = StoriesViewModel(repository)

        viewModel.onStart()
        advanceUntilIdle()

        val state = assertIs<StoriesState.Success>(viewModel.state.value)
        assertEquals(listOf("First", "Second"), state.stories.map { it.title })
    }

    @Test
    fun `successful but empty response is an error with NO_DATA`() = runTest(dispatcher) {
        repository.fetchResult = emptyList()
        val viewModel = StoriesViewModel(repository)

        viewModel.onStart()
        advanceUntilIdle()

        assertEquals(StoriesState.Error(StoriesError.NO_DATA), viewModel.state.value)
    }

    @Test
    fun `failed fetch with empty cache reports the real reason`() = runTest(dispatcher) {
        repository.fetchError = StoriesException(StoriesError.NO_INTERNET)
        val viewModel = StoriesViewModel(repository)

        viewModel.onStart()
        advanceUntilIdle()

        assertEquals(StoriesState.Error(StoriesError.NO_INTERNET), viewModel.state.value)
    }

    @Test
    fun `unexpected throwable is reported as UNKNOWN`() = runTest(dispatcher) {
        repository.fetchError = IllegalStateException("boom")
        val viewModel = StoriesViewModel(repository)

        viewModel.onStart()
        advanceUntilIdle()

        assertEquals(StoriesState.Error(StoriesError.UNKNOWN), viewModel.state.value)
    }

    @Test
    fun `failed fetch does not wipe out already cached stories`() = runTest(dispatcher) {
        repository.seed(StoriesSection.HOME, listOf(story("Cached")))
        repository.fetchError = StoriesException(StoriesError.TIMEOUT)
        val viewModel = StoriesViewModel(repository)

        viewModel.onStart()
        advanceUntilIdle()

        val state = assertIs<StoriesState.Success>(viewModel.state.value)
        assertEquals(listOf("Cached"), state.stories.map { it.title })
    }

    @Test
    fun `database failure is reported as STORAGE`() = runTest(dispatcher) {
        repository.storageError = StoriesException(StoriesError.STORAGE)
        val viewModel = StoriesViewModel(repository)

        viewModel.onStart()
        advanceUntilIdle()

        assertEquals(StoriesState.Error(StoriesError.STORAGE), viewModel.state.value)
    }

    @Test
    fun `switching section reloads it and keeps the sections independent`() = runTest(dispatcher) {
        repository.seed(StoriesSection.HOME, listOf(story("Home story")))
        repository.seed(StoriesSection.ARTS, listOf(story("Arts story")))
        val viewModel = StoriesViewModel(repository)

        viewModel.onStart()
        advanceUntilIdle()
        repository.fetchResult = listOf(story("Arts story"))
        viewModel.updateSection(StoriesSection.ARTS)
        advanceUntilIdle()

        assertEquals(StoriesSection.ARTS, viewModel.currentSection.value)
        assertEquals(listOf(StoriesSection.HOME, StoriesSection.ARTS), repository.fetchedSections)
        val state = assertIs<StoriesState.Success>(viewModel.state.value)
        assertEquals(listOf("Arts story"), state.stories.map { it.title })
    }

    @Test
    fun `recovered section drops the previous error`() = runTest(dispatcher) {
        repository.fetchError = StoriesException(StoriesError.SERVER)
        val viewModel = StoriesViewModel(repository)

        viewModel.onStart()
        advanceUntilIdle()
        assertEquals(StoriesState.Error(StoriesError.SERVER), viewModel.state.value)

        repository.fetchError = null
        repository.fetchResult = listOf(story("Back online"))
        viewModel.onRefresh()
        advanceUntilIdle()

        val state = assertIs<StoriesState.Success>(viewModel.state.value)
        assertEquals(listOf("Back online"), state.stories.map { it.title })
    }

    @Test
    fun `onRefresh re-fetches the current section`() = runTest(dispatcher) {
        val viewModel = StoriesViewModel(repository)

        viewModel.onStart()
        advanceUntilIdle()
        viewModel.onRefresh()
        advanceUntilIdle()

        assertEquals(listOf(StoriesSection.HOME, StoriesSection.HOME), repository.fetchedSections)
    }

    @Test
    fun `onStart loads only once`() = runTest(dispatcher) {
        val viewModel = StoriesViewModel(repository)

        viewModel.onStart()
        advanceUntilIdle()
        viewModel.onStart()
        advanceUntilIdle()

        assertEquals(listOf(StoriesSection.HOME), repository.fetchedSections)
    }

    @Test
    fun `isRefreshing is raised for the duration of the fetch`() = runTest(dispatcher) {
        val gate = CompletableDeferred<Unit>()
        repository.fetchGate = gate
        repository.fetchResult = listOf(story("Loaded"))
        val viewModel = StoriesViewModel(repository)

        viewModel.onStart()
        advanceUntilIdle()
        assertTrue(viewModel.isRefreshing.value, "флаг поднят, пока запрос не завершился")

        gate.complete(Unit)
        advanceUntilIdle()
        assertFalse(viewModel.isRefreshing.value, "флаг опущен после завершения запроса")
    }
}
