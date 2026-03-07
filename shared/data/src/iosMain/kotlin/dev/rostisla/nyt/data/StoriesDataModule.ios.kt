package dev.rostisla.nyt.data

import dev.rostisla.nyt.data.database.NytDatabase
import dev.rostisla.nyt.data.database.getRoomDatabase
import org.koin.core.scope.Scope

internal actual fun Scope.getDatabase(): NytDatabase {
    return getRoomDatabase(getDatabaseBuilder())


}