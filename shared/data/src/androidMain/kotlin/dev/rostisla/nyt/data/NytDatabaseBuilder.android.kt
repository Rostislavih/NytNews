package dev.rostisla.nyt.data

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import dev.rostisla.nyt.data.database.NytDatabase

internal fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<NytDatabase> {
    val appContext = context.applicationContext
    val dbFile = appContext.getDatabasePath("nyt.db")
    return Room.databaseBuilder<NytDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
}
