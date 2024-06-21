plugins {
    kotlin("jvm") version "1.9.24"
    kotlin("plugin.serialization") version "1.9.24"
    id("app.cash.sqldelight") version "2.0.2"
    idea
    application
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
    implementation(libs.ktor.server.statusPages)
    implementation(libs.ktor.server.websockets)

    implementation(libs.ktoml.core)

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
    jvmToolchain(17)
}

tasks {
    test {
        useJUnitPlatform()
    }

    processResources {
        from(projectDir.resolveSibling("frontend").resolve("dist")) {
            into("frontend")
        }
    }

    val buildFrontend by registering {
        group = "distribution"
        doFirst {
            exec {
                workingDir(projectDir.resolveSibling("frontend"))
                commandLine("pnpm", "run", "build")
            }
        }
    }

    register("packageFullstackApp") {
        group = "distribution"
        dependsOn(buildFrontend)
        finalizedBy(build, assembleDist)
    }

    register("runFullstackApp") {
        group = "application"
        dependsOn(buildFrontend)
        finalizedBy(build, run)
    }
}

application {
    mainClass.set("de.mfcrossendorf.logbook.ApplicationKt")
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
