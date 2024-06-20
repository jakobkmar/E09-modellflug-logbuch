package de.mfcrossendorf.logbook.database

import app.cash.sqldelight.ExecutableQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// We currently are not using R2DBC for simplicity.
// The following extension functions allow using blocking
// queries in a suspending coroutine context.

suspend fun <RowType : Any> ExecutableQuery<RowType>.awaitSingleOrNull(): RowType? {
    return withContext(Dispatchers.IO) {
        executeAsOneOrNull()
    }
}

suspend fun <RowType : Any> ExecutableQuery<RowType>.awaitList(): List<RowType> {
    return withContext(Dispatchers.IO) {
        executeAsList()
    }
}
