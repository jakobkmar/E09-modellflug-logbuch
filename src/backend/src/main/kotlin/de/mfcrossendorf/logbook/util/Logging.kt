package de.mfcrossendorf.logbook.util

import org.slf4j.LoggerFactory

typealias Logger = io.ktor.util.logging.Logger

val log: Logger = LoggerFactory.getLogger("Backend")
