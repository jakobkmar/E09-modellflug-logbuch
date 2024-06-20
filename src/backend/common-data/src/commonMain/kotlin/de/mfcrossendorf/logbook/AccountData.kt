package de.mfcrossendorf.logbook

import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@Serializable
@JsExport
data class LoginRequest(
    val username: String,
    val password: String,
)

@Serializable
@JsExport
class LoginResponse(
    val username: String,
    val isAdminUnsafe: Boolean,
)
