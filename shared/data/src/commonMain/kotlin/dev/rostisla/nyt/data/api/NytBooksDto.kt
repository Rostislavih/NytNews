package dev.rostisla.nyt.data.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal class NytOverviewResponseDto(
    @SerialName("status") val status: String,
    @SerialName("copyright") val copyright: String? = null,
    @SerialName("num_results") val numResults: Int? = null,
    @SerialName("results") val results: NytOverviewResultsDto
)

@Serializable
internal class NytOverviewResultsDto(
    @SerialName("published_date") val publishedDate: String,
    @SerialName("previous_published_date") val previousPublishedDate: String? = null,
    @SerialName("next_published_date") val nextPublishedDate: String? = null,
    @SerialName("lists") val lists: List<NytOverviewListDto>
)

@Serializable
internal class NytOverviewListDto(
    @SerialName("display_name") val displayName: String,
    @SerialName("list_name_encoded") val listNameEncoded: String,
    @SerialName("updated") val updated: String,
    @SerialName("books") val books: List<NytBookDto>
)

@Serializable
internal class NytListResponseDto(
    @SerialName("status") val status: String,
    @SerialName("copyright") val copyright: String? = null,
    @SerialName("num_results") val numResults: Int? = null,
    @SerialName("results") val results: NytListResultDto
)

@Serializable
internal class NytListResultDto(
    @SerialName("display_name") val displayName: String,
    @SerialName("list_name_encoded") val listNameEncoded: String,
    @SerialName("published_date") val publishedDate: String,
    @SerialName("updated") val updated: String,
    @SerialName("previous_published_date") val previousPublishedDate: String? = null,
    @SerialName("next_published_date") val nextPublishedDate: String? = null,
    @SerialName("books") val books: List<NytBookDto>
)

@Serializable
internal class NytBookDto(
    @SerialName("rank") val rank: Int,
    @SerialName("rank_last_week") val rankLastWeek: Int? = null,
    @SerialName("weeks_on_list") val weeksOnList: Int? = null,
    @SerialName("asterisk") val asterisk: Int? = null,
    @SerialName("dagger") val dagger: Int? = null,
    @SerialName("primary_isbn13") val primaryIsbn13: String? = null,
    @SerialName("publisher") val publisher: String? = null,
    @SerialName("description") val description: String? = null,
    @SerialName("title") val title: String,
    @SerialName("author") val author: String? = null,
    @SerialName("contributor") val contributor: String? = null,
    @SerialName("book_image") val bookImage: String? = null,
    @SerialName("amazon_product_url") val amazonProductUrl: String? = null,
    @SerialName("age_group") val ageGroup: String? = null,
    @SerialName("book_review_link") val bookReviewLink: String? = null,
    @SerialName("sunday_review_link") val sundayReviewLink: String? = null
)
