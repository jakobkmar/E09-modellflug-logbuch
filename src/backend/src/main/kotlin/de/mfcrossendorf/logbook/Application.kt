package de.mfcrossendorf.logbook

import de.mfcrossendorf.logbook.db.Database
import de.mfcrossendorf.logbook.routes.adminRoutes
import de.mfcrossendorf.logbook.routes.flightlogRoutes
import de.mfcrossendorf.logbook.routes.profileRoutes
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import org.postgresql.util.PSQLException

fun main() {
    // connect to the database
    database

    // create the database schema if it doesn't exist TODO use migrations
    try {
        Database.Schema.create(database.driver)
    } catch (exc: PSQLException) {
        println("Database schema already exists: '${exc::class.simpleName}'")
    }

    // configure and start the server
    embeddedServer(Netty, port = 8080) {
        routing {
            profileRoutes()
            flightlogRoutes()
            adminRoutes()
        }
    }.start(wait = true)
}
