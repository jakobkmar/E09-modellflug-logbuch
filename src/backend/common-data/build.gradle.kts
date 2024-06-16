plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

kotlin {
    js(IR) {
        browser()
        binaries.library()
        useEsModules()
        generateTypeScriptDefinitions()
    }
    jvm()

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlinx.serialization.core)
                implementation(libs.kotlinx.datetime)
            }
        }
    }
}
