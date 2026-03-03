package dev.rostisla.nyt.presentation.mapper

import dev.rostisla.nyt.domain.model.Story
import dev.rostisla.nyt.presentation.model.UiStory

internal fun Story.toUiStory(): UiStory {
    return UiStory(
        title = title,
        abstract = abstract,
        publishedDate = publishedDate,
    )
}
