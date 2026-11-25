/*
 * Static API check of the shared modules (ADR-0044, item 5; docs/android-compatibility.md).
 *
 * This library compiles the sources of every shared module (listed by settings.gradle.kts) against android.jar,
 * minimum API 21, with core library desugaring, and runs Android lint on them with NewApi as an error: a call to a
 * Java API that API 21 does not have and that desugar_jdk_libs does not provide fails the build. A type that
 * Android does not have at all (java.desktop: AWT, Swing, ImageIO, javax.sound) fails the compilation. It is never
 * published: the shared modules are dexed from their Maven artifacts by the verification application.
 */
plugins {
    alias(libs.plugins.android.library)
}

val repositoryRoot = gradle.extra["lionengine.repositoryRoot"] as File
@Suppress("UNCHECKED_CAST")
val sharedModules = gradle.extra["lionengine.sharedModules"] as List<String>

android {
    namespace = "com.b3dgs.lionengine.android.lint"
    compileSdk = 37

    defaultConfig {
        minSdk = 21
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    sourceSets {
        getByName("main") {
            java.srcDirs(sharedModules.map { repositoryRoot.resolve("$it/src/main/java") })
        }
    }

    lint {
        // ADR-0044: every call above API 21 that desugaring does not cover is an error, fixed and never suppressed.
        error += "NewApi"
        abortOnError = true
        checkReleaseBuilds = false
        // Versions are pinned (ADR-0012) and bumped as recorded dependency changes, not on lint's advice.
        disable += setOf("AndroidGradlePluginVersion", "GradleDependency", "NewerVersionAvailable")
    }
}

dependencies {
    coreLibraryDesugaring(libs.desugar.jdk.libs)
}
