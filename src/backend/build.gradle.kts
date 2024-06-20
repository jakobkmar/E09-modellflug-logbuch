plugins {
    kotlin("jvm") version "1.9.23"
    kotlin("plugin.serialization") version "1.9.23"
    id("app.cash.sqldelight") version "2.0.2"
    idea
}

allprojects {
    group = "de.mfcrossendorf"
    version = "0.0.1"

    repositories {
        mavenCentral()
    }
}

dependencies {
    implementation(project(":common-data"))

    testImplementation(kotlin("test"))
    implementation(libs.kotlinx.datetime)

    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.sessions)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.contentNegotiation)
    implementation(libs.ktor.serialization.json)
    implementation(libs.ktor.server.cors)

    // logging
    implementation(libs.slf4j.simple)
    implementation(libs.slf4j.jclOverSlf4j)
    implementation(libs.spring.security.crypto) // password hashing
    implementation(libs.bouncycastle)

    // database dependencies
    implementation(libs.hikaricp)
    implementation(libs.postgresql)
    implementation(libs.sqldelight.jdbcDriver)
}

kotlin {
    jvmToolchain(21)
}

tasks {
    test {
        useJUnitPlatform()
    }
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}

sqldelight {
    databases {
        create("Database") {
            packageName.set("de.mfcrossendorf.logbook.db")
            dialect(libs.sqldelight.dialect.postgresql)
        }
    }
}
