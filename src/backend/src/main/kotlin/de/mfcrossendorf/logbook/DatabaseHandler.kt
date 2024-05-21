package de.mfcrossendorf.logbook

import app.cash.sqldelight.db.SqlDriver
import de.mfcrossendorf.Database

private lateinit var driver: SqlDriver
val database = Database(driver)
