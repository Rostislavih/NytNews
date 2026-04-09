package dev.rostisla.nyt.domain.model

class Story(
    val url: String,
    val title: String,
    val abstract: String,
    val publishedDate: String,
    val imageUrl: String? = null
)
