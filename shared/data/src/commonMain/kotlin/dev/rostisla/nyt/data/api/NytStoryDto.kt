package dev.rostisla.nyt.data.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal class NytStoryDto(
    @SerialName("url") val url: String,
    @SerialName("title") val title: String,
    @SerialName("abstract") val abstract: String,
    @SerialName("published_date") val publishedDate: String,
    @SerialName("multimedia") val multimedia: List<NytMultimediaDto>? = null
)

@Serializable
internal class NytMultimediaDto(
    @SerialName("url") val url: String,
    @SerialName("format") val format: String,
    @SerialName("type") val type: String
)
