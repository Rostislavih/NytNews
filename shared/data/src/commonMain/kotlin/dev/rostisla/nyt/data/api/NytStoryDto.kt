package dev.rostisla.nyt.data.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal class NytStoryDto(
    @SerialName("title") val title: String,
    @SerialName("abstract") val abstract: String,
    @SerialName("published_date") val publishedDate: String,
)
