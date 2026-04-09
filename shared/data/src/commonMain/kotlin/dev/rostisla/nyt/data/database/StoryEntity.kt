package dev.rostisla.nyt.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import io.ktor.http.Url

internal const val STORY_TABLE_NAME = "stories"
internal const val STORY_URL = "url"
internal const val STORY_TITLE = "title"
internal const val STORY_ABSTRACT = "abstract"
internal const val STORY_PUBLISHED_DATE = "published_date"
internal const val STORY_SECTION = "section"
internal const val STORY_IMAGE_URL = "image_url"

@Entity(tableName = STORY_TABLE_NAME)
internal class StoryEntity(
    @ColumnInfo(name = STORY_URL)
    @PrimaryKey val url: String,
    @ColumnInfo(name = STORY_TITLE)
    val title: String,
    @ColumnInfo(name = STORY_ABSTRACT)
    val abstract: String,
    @ColumnInfo(name = STORY_PUBLISHED_DATE)
    val publishedDate: String,
    @ColumnInfo(name = STORY_SECTION)
    val section: String,
    @ColumnInfo(name = STORY_IMAGE_URL)
    val imageUrl: String? = null
)
