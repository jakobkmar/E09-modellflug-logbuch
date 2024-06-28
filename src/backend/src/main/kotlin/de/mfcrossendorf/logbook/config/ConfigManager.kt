package de.mfcrossendorf.logbook.config

import com.akuleshov7.ktoml.Toml
import com.akuleshov7.ktoml.TomlIndentation
import com.akuleshov7.ktoml.TomlInputConfig
import com.akuleshov7.ktoml.TomlOutputConfig
import de.mfcrossendorf.logbook.util.log
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlin.io.path.*
import kotlin.system.exitProcess

object ConfigManager {
    private val path = Path("./config/config.toml")

    lateinit var config: AppConfig
        private set

    val isDevelopmentMode: Boolean
        get() = config.server.developmentMode

    fun loadConfig() {
        log.info("Loading configuration from ${path.absolutePathString()}")

        config = if (path.exists()) {
            try {
                Toml(inputConfig = TomlInputConfig(ignoreUnknownNames = true))
                    .decodeFromString(path.readText())
            } catch (exc: SerializationException) {
                log.error("Failed to decode configuration at ${path.absolutePathString()}")
                throw exc
            }
        } else {
            val defaultConfig = AppConfig()
            path.createParentDirectories()
            val defaultToml = Toml(
                outputConfig = TomlOutputConfig(
                    ignoreNullValues = false,
                    indentation = TomlIndentation.TWO_SPACES)
            ).encodeToString(defaultConfig)
            path.writeText(defaultToml)
            log.info("Created default configuration at ${path.absolutePathString()}")
            log.info("Please edit the configuration file and restart the application!")
            exitProcess(0)
        }
    }
}
