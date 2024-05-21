package de.mfcrossendorf.logbook

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import de.mfcrossendorf.logbook.routes.adminRoutes
import de.mfcrossendorf.logbook.routes.flightlogRoutes
import de.mfcrossendorf.logbook.routes.profileRoutes

fun main() {
    embeddedServer(Netty, port = 8080) {
        routing {
            profileRoutes()
            flightlogRoutes()
            adminRoutes()
        }
    }.start(wait = true)
}
