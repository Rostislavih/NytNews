package dev.rostisla.nyt.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
internal interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(story: List<StoryEntity>)

    @Query("SELECT * FROM $STORY_TABLE_NAME WHERE $STORY_SECTION = :section")
    suspend fun getAllBySection(section: String): List<StoryEntity>

    @Query("SELECT * FROM $STORY_TABLE_NAME WHERE $STORY_SECTION = :section")
    fun getAllAsFlowBySection(section: String): Flow<List<StoryEntity>>

    @Query("DELETE FROM $STORY_TABLE_NAME WHERE $STORY_SECTION = :section")
    suspend fun clearBySection(section: String)

    @Query("DELETE FROM $STORY_TABLE_NAME")
    suspend fun clearAll()
}
