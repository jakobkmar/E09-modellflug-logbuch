package de.mfcrossendorf.logbook

import de.mfcrossendorf.logbook.routes.adminRoutes
import de.mfcrossendorf.logbook.routes.flightlogRoutes
import de.mfcrossendorf.logbook.routes.profileRoutes
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*

fun main() {
    // connect to the database
    database

    // configure and start the server
    embeddedServer(Netty, port = 8080) {
        routing {
            profileRoutes()
            flightlogRoutes()
            adminRoutes()
        }
    }.start(wait = true)
}
