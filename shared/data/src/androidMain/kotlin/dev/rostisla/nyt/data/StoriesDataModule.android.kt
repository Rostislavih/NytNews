package dev.rostisla.nyt.data

import android.content.Context
import dev.rostisla.nyt.data.database.NytDatabase
import dev.rostisla.nyt.data.database.getRoomDatabase
import org.koin.core.scope.Scope

internal actual fun Scope.getDatabase(): NytDatabase {
    val context: Context = get()
    return getRoomDatabase(getDatabaseBuilder(context))

}