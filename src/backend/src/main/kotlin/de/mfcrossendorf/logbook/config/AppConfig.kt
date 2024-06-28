package de.mfcrossendorf.logbook.config

import com.akuleshov7.ktoml.annotations.TomlComments
import kotlinx.datetime.TimeZone
import kotlinx.serialization.Serializable

@Serializable
data class AppConfig(
    @TomlComments(
        "The time zone to use for the application.",
        "If not set, the system default is used.",
        "See https://kotlinlang.org/api/kotlinx-datetime/kotlinx-datetime/kotlinx.datetime/-time-zone/-companion/of.html for more information."
    )
    val timeZone: TimeZone? = null,
    val database: DatabaseConfig = DatabaseConfig(),
    val server: ServerConfig = ServerConfig(),
) {
    @Serializable
    data class DatabaseConfig(
        val serverName: String = "localhost",
        @TomlComments("The PostgreSQL default port is 5432.")
        val port: Int = 5432,
        val username: String = "logbook",
        val password: String = "SET_PASSWORD_HERE",
        @TomlComments("The name of the database to use. (You chose this during database setup.)")
        val databaseName: String = "logbook",
    )

    @Serializable
    data class ServerConfig(
        @TomlComments("The port the server should listen on.")
        val port: Int = 8080,
        @TomlComments(
            "In development mode the server runs without many security features.",
            "This is useful for testing and debugging in local environments."
        )
        val developmentMode: Boolean = false,
    )
}
