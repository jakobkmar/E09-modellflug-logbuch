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
    jvm {
        jvmToolchain(17)
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlinx.serialization.core)
                implementation(libs.kotlinx.datetime)
            }
        }

        all {
            languageSettings {
                optIn("kotlin.js.ExperimentalJsExport")
            }
        }
    }
}
