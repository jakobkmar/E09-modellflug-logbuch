package de.mfcrossendorf.logbook.validation

import de.mfcrossendorf.logbook.CreateFlightLogRequest
import de.mfcrossendorf.logbook.util.today
import kotlinx.datetime.Clock

fun CreateFlightLogRequest.validateAndTrim(): CreateFlightLogRequest {
    return CreateFlightLogRequest(
        date = date,
        flightStart = flightStart,
        signature = signature.trim(),
        checkedFirstAid = checkedFirstAid,
        modelType = modelType.trim(),
    ).apply { validate() }
}

private fun CreateFlightLogRequest.validate() {
    val issues = buildList {
        if (signature.isBlank())
            add("Signature must not be empty")
        if (modelType.isBlank())
            add("Model type must not be empty")
        if (date > Clock.System.today())
            add("Cannot create flight log for future date")
    }.ifEmpty { null }

    if (issues != null) {
        throw ValidationException(issues.joinToString("\n"))
    }
}
