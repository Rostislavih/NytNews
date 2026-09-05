package dev.rostisla.nyt.presentation.mapper

import dev.rostisla.nyt.domain.model.Story
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class StoryToUiStoryMapperTest {

    @Test
    fun `story fields are carried over as is`() {
        val story = Story(
            url = "https://nyt.com/story",
            title = "Title",
            abstract = "Abstract",
            publishedDate = "2026-09-05T10:00:00-04:00",
            imageUrl = "https://nyt.com/image.jpg"
        )

        val ui = story.toUiStory()

        assertEquals("Title", ui.title)
        assertEquals("Abstract", ui.abstract)
        assertEquals("https://nyt.com/image.jpg", ui.imageUrl)
    }

    @Test
    fun `iso date is reformatted for display`() {
        val story = story(publishedDate = "2026-09-05T10:00:00-04:00")

        val formatted = story.toUiStory().publishedDate

        // Часовой пояс машины неизвестен, поэтому проверяем форму, а не точное время.
        assertTrue(
            formatted.matches(Regex("""\d{2}:\d{2} \d{2} [A-Z][a-z]{2} \d{4}""")),
            "ожидали формат 'HH:mm dd Mon yyyy', получили '$formatted'"
        )
        assertTrue(formatted.endsWith("2026"), "год должен сохраниться: '$formatted'")
    }

    @Test
    fun `unparseable date falls back to the raw value`() {
        val story = story(publishedDate = "Author: Frank Herbert")

        assertEquals("Author: Frank Herbert", story.toUiStory().publishedDate)
    }

    @Test
    fun `missing image stays null`() {
        assertEquals(null, story().toUiStory().imageUrl)
    }

    private fun story(publishedDate: String = "2026-09-05T10:00:00-04:00") = Story(
        url = "https://nyt.com/story",
        title = "Title",
        abstract = "Abstract",
        publishedDate = publishedDate
    )
}
