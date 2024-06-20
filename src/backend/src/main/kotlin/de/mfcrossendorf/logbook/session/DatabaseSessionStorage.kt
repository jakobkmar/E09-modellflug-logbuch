package de.mfcrossendorf.logbook.session

import de.mfcrossendorf.logbook.database.awaitSingleOrNull
import de.mfcrossendorf.logbook.db.Database
import io.ktor.server.sessions.*

class DatabaseSessionStorage(private val db: Database) : SessionStorage {

    override suspend fun invalidate(id: String) {
        db.sessionQueries.deleteSession(id)
            .awaitSingleOrNull() ?:
            throw NoSuchElementException("Cannot invalidate session '$id' because it does not exist in database")
    }

    override suspend fun read(id: String): String {
        return db.sessionQueries.getSessionData(id)
            .awaitSingleOrNull() ?:
            throw NoSuchElementException("Session '$id' not found in database")
    }

    override suspend fun write(id: String, value: String) {
        val updatedId = db.sessionQueries.upsertSession(id, value)
            .awaitSingleOrNull()
        check(updatedId != null) { "Session data for '$id' was not written to the database" }
        check(updatedId == id) { "Session '$updatedId' changed during write instead of '$id'" }
    }
}
