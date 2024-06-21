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
class SharedSessionData(
    val userId: Int,
    val username: String,
    /**
     * this is only for conditional display purposes,
     * not for actual security checks
     */
    val isAdminUnsafe: Boolean,
)

@Serializable
@JsExport
class AccountResponse(
    val userId: Int,
    val username: String,
    val firstName: String,
    val lastName: String?,
    val isAdminUnsafe: Boolean,
    val registrationNumber: String?,
)
