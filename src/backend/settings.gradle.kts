rootProject.name = "modellflug-logbuch"

include(":common-data")

fun VersionCatalogBuilder.libs() {
    val serializationVersion = "1.6.3"
    library(
        "kotlinx.serialization.core",
        "org.jetbrains.kotlinx:kotlinx-serialization-core:$serializationVersion")
    library(
        "kotlinx.serialization.json",
        "org.jetbrains.kotlinx:kotlinx-serialization-json:$serializationVersion")
    library(
        "kotlinx.datetime",
        "org.jetbrains.kotlinx:kotlinx-datetime:0.6.0")

    val ktorVersion = "2.3.11"
    library(
        "ktor.server.core",
        "io.ktor:ktor-server-core:$ktorVersion")
    library(
        "ktor.server.netty",
        "io.ktor:ktor-server-netty:$ktorVersion")
    library(
        "ktor.server.sessions",
        "io.ktor:ktor-server-sessions:$ktorVersion")
    library(
        "ktor.server.auth",
        "io.ktor:ktor-server-auth:$ktorVersion")
    library(
        "ktor.server.contentNegotiation",
        "io.ktor:ktor-server-content-negotiation:$ktorVersion")
    library(
        "ktor.serialization.json",
        "io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")

    library(
        "slf4j.simple",
        "org.slf4j:slf4j-simple:2.0.12")
    library(
        "slf4j.jclOverSlf4j",
        "org.slf4j:jcl-over-slf4j:2.0.12")
    library(
        "spring-security-crypto",
        "org.springframework.security:spring-security-crypto:6.3.0")

    library(
        "hikaricp",
        "com.zaxxer:HikariCP:5.1.0")
    library(
        "postgresql",
        "org.postgresql:postgresql:42.7.3")

    val sqldelightVersion = "2.0.2"
    library(
        "sqldelight.jdbcDriver",
        "app.cash.sqldelight:jdbc-driver:$sqldelightVersion")
    library(
        "sqldelight.dialect.postgresql",
        "app.cash.sqldelight:postgresql-dialect:$sqldelightVersion")
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            libs()
        }
    }
}
