plugins {
    kotlin("jvm") version "1.9.23"
    id("app.cash.sqldelight") version "2.0.2"
    idea
}

group = "de.mfcrossendorf"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")

    val ktorVersion = "2.3.9"
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")

    implementation("org.slf4j:slf4j-simple:2.0.12") // logging

    // database dependencies
    implementation("com.zaxxer:HikariCP:5.1.0")
    implementation("app.cash.sqldelight:jdbc-driver:2.0.2")
    implementation("org.postgresql:postgresql:42.7.3")
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
            dialect("app.cash.sqldelight:postgresql-dialect:2.0.2")
        }
    }
}
