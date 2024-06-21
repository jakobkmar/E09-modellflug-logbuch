package de.mfcrossendorf.logbook.validation

import io.ktor.http.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

class ValidationException(val msg: String) : Exception(msg)

fun StatusPagesConfig.validationExceptionHandler() {
    exception<ValidationException> { call, cause ->
        call.respond(HttpStatusCode.BadRequest, cause.msg)
    }
}
