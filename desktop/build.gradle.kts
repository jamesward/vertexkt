plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

kotlin {
    jvm()

    sourceSets {
        named("jvmMain") {
            dependencies {
                implementation(project(":core"))
                implementation(compose.desktop.currentOs)
            }
        }
    }
}

compose.desktop {
    application {
        args.add("asdf")

        mainClass = "com.jamesward.vertexkt.desktop.MainKt"
        nativeDistributions {
            targetFormats(org.jetbrains.compose.desktop.application.dsl.TargetFormat.Dmg, org.jetbrains.compose.desktop.application.dsl.TargetFormat.Msi, org.jetbrains.compose.desktop.application.dsl.TargetFormat.Deb)
            modules("java.net.http")
            modules("jdk.crypto.ec")
            packageName = "VertexKt"
            packageVersion = System.getenv()["REF"]?.removePrefix("refs/tags/v") ?: "255.255.65535"
            macOS {
                bundleID = System.getenv()["ASC_BUNDLE_ID"]
                signing {
                    sign.set(System.getenv()["DESKTOP_CERT_NAME"] != null)
                    identity.set(System.getenv()["DESKTOP_CERT_NAME"])
                }
                notarization {
                    appleID.set(System.getenv()["NOTARIZATION_APPLEID"])
                    password.set(System.getenv()["NOTARIZATION_PASSWORD"])
                }
            }
        }
    }
}