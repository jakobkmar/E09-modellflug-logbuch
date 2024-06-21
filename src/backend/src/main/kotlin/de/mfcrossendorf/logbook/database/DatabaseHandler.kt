package de.mfcrossendorf.logbook.database

import app.cash.sqldelight.driver.jdbc.asJdbcDriver
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import de.mfcrossendorf.logbook.config.ConfigManager
import de.mfcrossendorf.logbook.db.Database
import java.util.*

private val dbDriver = run {
    val dbConfig = ConfigManager.config.database

    val props = Properties()
    props.setProperty("dataSourceClassName", "org.postgresql.ds.PGSimpleDataSource")
    props.setProperty("dataSource.user", dbConfig.username)
    props.setProperty("dataSource.password", dbConfig.password)
    props.setProperty("dataSource.databaseName", dbConfig.databaseName)
    props.setProperty("dataSource.portNumber", dbConfig.port.toString())
    props.setProperty("dataSource.serverName", dbConfig.serverName)

    val config = HikariConfig(props)
    HikariDataSource(config).asJdbcDriver()
}

val database = Database(dbDriver)

@Suppress("UnusedReceiverParameter")
val Database.driver
    get() = dbDriver
