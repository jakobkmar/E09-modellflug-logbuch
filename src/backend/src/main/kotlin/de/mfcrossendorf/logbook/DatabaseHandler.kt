package de.mfcrossendorf.logbook

import app.cash.sqldelight.driver.jdbc.asJdbcDriver
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import de.mfcrossendorf.logbook.db.Database
import java.util.*
import kotlin.io.path.*

val database: Database = run {
    val props = Properties()
    props.setProperty("dataSourceClassName", "org.postgresql.ds.PGSimpleDataSource")
    props.setProperty("dataSource.user", "logbook")
    props.setProperty("dataSource.password", "SET_PASSWORD_HERE")
    props.setProperty("dataSource.databaseName", "logbook")
    props.setProperty("dataSource.portNumber", "5432")
    props.setProperty("dataSource.serverName", "localhost")

    val configPath = Path("./config/database.properties")
    if (configPath.notExists()) {
        configPath.createParentDirectories()
        configPath.outputStream().use { stream ->
            props.store(stream, "Database configuration for logbook backend")
        }
    }

    val config = HikariConfig(configPath.absolutePathString())
    Database(HikariDataSource(config).asJdbcDriver())
}
