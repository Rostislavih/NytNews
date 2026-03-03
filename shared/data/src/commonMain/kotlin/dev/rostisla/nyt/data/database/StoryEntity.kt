package dev.rostisla.nyt.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

internal const val STORY_TABLE_NAME = "stories"
internal const val STORY_ID = "id"
internal const val STORY_TITLE = "title"
internal const val STORY_ABSTRACT = "abstract"
internal const val STORY_PUBLISHED_DATE = "published_date"

@Entity(tableName = STORY_TABLE_NAME)
internal class StoryEntity(
    @ColumnInfo(name = STORY_ID)
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = STORY_TITLE)
    val title: String,
    @ColumnInfo(name = STORY_ABSTRACT)
    val abstract: String,
    @ColumnInfo(name = STORY_PUBLISHED_DATE)
    val publishedDate: String,
)