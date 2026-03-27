package dev.rostisla.nyt.presentation.mapper

import dev.rostisla.nyt.domain.model.Story
import dev.rostisla.nyt.presentation.model.UiStory
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime


internal fun Story.toUiStory(): UiStory {
    return UiStory(
        title = title,
        abstract = abstract,
        publishedDate = formatIsoDate(publishedDate),
        imageUrl = imageUrl
    )
}

private fun formatIsoDate(isoString: String): String {
    return try {
        val instant = Instant.parse(isoString)
        val dt = instant.toLocalDateTime(TimeZone.currentSystemDefault())

        val time = "${dt.hour.pad()}:${dt.minute.pad()}"
        val day = dt.dayOfMonth.pad()
        val month = dt.month.name.lowercase()
            .replaceFirstChar { it.uppercase() }
            .take(3)

        "$time $day $month ${dt.year}"
    } catch (e: Exception) {
        isoString
    }
}


private fun Int.pad() = this.toString().padStart(2, '0')