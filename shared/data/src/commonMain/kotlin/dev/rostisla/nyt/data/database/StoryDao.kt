package dev.rostisla.nyt.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
internal interface StoryDao {
    @Insert
    suspend fun insert(story: List<StoryEntity>)

    @Query("SELECT * FROM $STORY_TABLE_NAME")
    suspend fun getAll(): List<StoryEntity>

    @Query("SELECT * FROM $STORY_TABLE_NAME")
    fun getAllAsFlow(): Flow<List<StoryEntity>>
}
