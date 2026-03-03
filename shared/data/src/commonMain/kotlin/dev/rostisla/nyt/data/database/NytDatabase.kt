package dev.rostisla.nyt.data.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

@Database(entities = [StoryEntity::class], version = 1)
@ConstructedBy(NytDatabaseConstructor::class)
internal abstract class NytDatabase : RoomDatabase() {
    abstract fun storyDao(): StoryDao
}

@Suppress("KotlinNoActualForExpect")
internal expect object NytDatabaseConstructor : RoomDatabaseConstructor<NytDatabase> {
    override fun initialize(): NytDatabase
}

internal fun getRoomDatabase(
    builder: RoomDatabase.Builder<NytDatabase>
): NytDatabase {
    return builder
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}