package dev.rostisla.nyt.data.mapper

import dev.rostisla.nyt.data.database.StoryEntity
import dev.rostisla.nyt.domain.model.StoriesSection
import dev.rostisla.nyt.domain.model.Story

internal fun StoryEntity.toStory(): Story {
    return Story(
        title = this.title,
        abstract = this.abstract,
        publishedDate = this.publishedDate,
        imageUrl = this.imageUrl
    )
}

internal fun Story.toEntity(section: StoriesSection): StoryEntity {
    return StoryEntity(
        id = 0,
        title = this.title,
        abstract = this.abstract,
        publishedDate = this.publishedDate,
        section = section.name,
        imageUrl = this.imageUrl
    )
}
