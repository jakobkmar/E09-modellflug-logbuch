package de.mfcrossendorf.logbook.validation

import de.mfcrossendorf.logbook.CreateAccountRequest

fun CreateAccountRequest.validateAndTrim(): CreateAccountRequest {
    return copy(
        username = username.trim(),
        password = password?.trim(),
        firstName = firstName.trim(),
        lastName = lastName?.trim(),
        registrationNumber = registrationNumber?.trim(),
    ).apply { validate() }
}

private fun CreateAccountRequest.validate() {
    val issues = buildList {
        if (username.isBlank())
            add("Username must not be empty")
        if (username.any { it.isWhitespace() })
            add("Username must not contain whitespace")
        if (username.length < 3)
            add("Username must be at least 3 characters long")
        if (password != null && password!!.isBlank())
            add("If set, password must not be empty")
        if (password != null && password!!.any { it.isWhitespace() })
            add("Password must not contain whitespace")
        if (firstName.isBlank())
            add("First name must not be empty")
        if (registrationNumber != null && registrationNumber!!.isBlank())
            add("If set, registration number must not be empty")
    }.ifEmpty { null }

    if (issues != null) {
        throw ValidationException(issues.joinToString("\n"))
    }
}
