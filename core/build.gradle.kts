plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("com.android.library")
}

kotlin {
    jvmToolchain(17)

    android()

    jvm()

    js(IR) {
        browser()
    }

    @Suppress("OPT_IN_USAGE")
    wasm {
        browser()
    }

    ios()

    iosSimulatorArm64()

    sourceSets {
        getByName("commonMain") {
            dependencies {
                implementation("io.ktor:ktor-client-core:2.3.1-wasm0")
                //implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
                implementation("io.ktor:ktor-client-content-negotiation:2.3.1-wasm0")
                implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.1-wasm0")
                //implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.6.4")
            }
        }

        getByName("commonTest") {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        getByName("androidMain") {
            dependencies {
                implementation("io.ktor:ktor-client-android:2.3.1-wasm0")
            }
        }

        getByName("jvmMain") {
            dependencies {
                implementation("io.ktor:ktor-client-java:2.3.1-wasm0")
                runtimeOnly("org.slf4j:slf4j-simple:2.0.7")
            }
        }

        getByName("jsMain") {
            dependencies {
                implementation("io.ktor:ktor-client-js:2.3.1-wasm0")
            }
        }

        /*
        getByName("wasmMain") {
            dependencies {
                implementation("io.ktor:ktor-client-ios:2.3.1-wasm0")
            }
        }
         */

        getByName("iosMain") {
            dependencies {
                implementation("io.ktor:ktor-client-ios:2.3.1-wasm0")
            }
        }

    }
}

android {
    namespace = "com.jamesward.vertexkt.core"
    buildToolsVersion = "33.0.2"
    compileSdk = 33
    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility(JavaVersion.VERSION_17)
        targetCompatibility(JavaVersion.VERSION_17)
    }
}

tasks.withType<Test> {
    testLogging {
        showStandardStreams = true
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        events(org.gradle.api.tasks.testing.logging.TestLogEvent.STARTED, org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED, org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED, org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED)
    }
}
