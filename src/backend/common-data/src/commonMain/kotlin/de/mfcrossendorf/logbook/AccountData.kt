package de.mfcrossendorf.logbook

import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@Serializable
@JsExport
/**
 * A simple request to log in, used for initial session auth.
 */
class LoginRequest(
    /**
     * The username.
     */
    val username: String,
    /**
     * The password in plaintext.
     */
    val password: String,
)

@Serializable
@JsExport
/**
 * A simple response to a successful login.
 * Shares the session data stored in the backend with the client for display purposes only.
 * Not sent from the frontend back to the backend after that.
 */
class SharedSessionData(
    /**
     * This session's user ID.
     */
    val userId: Int,
    /**
     * This session's username.
     */
    val username: String,
    /**
     * this is only for conditional display purposes,
     * not for actual security checks
     */
    val isAdminUnsafe: Boolean,
    /**
     * this is only for conditional display purposes,
     * not for actual security checks
     */
    val canSeeAllLogsUnsafe: Boolean,
)

@Serializable
@JsExport
/**
 * A simple response to a successful login or for the user management pages.
 */
class AccountResponse(
    /**
     * The user ID of this account.
     */
    val userId: Int,
    /**
     * The username of this account.
     */
    val username: String,
    /**
     * The real first name of the user.
     */
    val firstName: String,
    /**
     * The real last name of the user or null if not set.
     */
    val lastName: String?,
    /**
     * The registration number of the user or null if not set.
     */
    val registrationNumber: String?,
    /**
     * Whether this account is an admin, only for display purposes.
     */
    val isAdminUnsafe: Boolean,
    /**
     * Whether this account can see all logs, only for display purposes.
     */
    val canSeeAllLogsUnsafe: Boolean,
)

@Serializable
@JsExport
/**
 * A simple request to create a new account.
 * The password is optional, as it can be chosen by the user on first login.
 */
class CreateAccountRequest(
    /**
     * The username for the new account.
     */
    val username: String,
    /**
     * The plaintext password for the new account.
     */
    val password: String?,
    /**
     * The real first name of the new user.
     */
    val firstName: String,
    /**
     * The real last name of the new user or null if none is known.
     */
    val lastName: String?,
    /**
     * The registration number of the new user or null if unknown.
     */
    val registrationNumber: String?,
    /**
     * Whether the new account is an admin.
     */
    val isAdmin: Boolean,
    /**
     * Whether the new account can see all logs.
     */
    val canSeeAllLogs: Boolean,
)
