package dev.rostisla.nyt.data.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal class NytStoriesDto(
    @SerialName("results") val results: List<NytStoryDto>
)
