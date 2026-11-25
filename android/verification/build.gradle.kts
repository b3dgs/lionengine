/*
 * DEX verification application (ADR-0012, ADR-0044, REQ-TECH-006, REQ-TECH-007; docs/android-compatibility.md).
 *
 * It depends on the shared modules installed in the local Maven repository (settings.gradle.kts), like a game does,
 * with core library desugaring and minimum API 21:
 * - the debug build runs D8 on them, the release build runs R8 with every engine class kept, and the tasks
 *   verifyDebugDex and verifyReleaseDex check that each class of each shared module is in the APK;
 * - the instrumentation tests (src/androidTest, AndroidX Test with JUnit 4, because JUnit 6 Jupiter runs on the JVM
 *   only, ADR-0050) run the shared modules and the Java feature probes on an API 21 emulator
 *   (connectedDebugAndroidTest).
 */
import com.android.build.api.artifact.SingleArtifact
import com.android.build.api.variant.BuiltArtifactsLoader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.zip.ZipFile

plugins {
    alias(libs.plugins.android.application)
}

val engineVersion = gradle.extra["lionengine.version"] as String
@Suppress("UNCHECKED_CAST")
val sharedModules = gradle.extra["lionengine.sharedModules"] as List<String>

android {
    namespace = "com.b3dgs.lionengine.android.verification"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.b3dgs.lionengine.android.verification"
        minSdk = 21
        targetSdk = 37
        versionCode = 1
        versionName = engineVersion
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    buildTypes {
        getByName("release") {
            // R8: shrinking and optimization on, every engine class kept so that all of them are processed.
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    lint {
        // The probes call APIs above API 21 on purpose; the shared code is checked by the api-lint module.
        ignoreTestSources = true
        disable += setOf("AndroidGradlePluginVersion", "GradleDependency", "NewerVersionAvailable")
    }
}

/** The JARs of the shared modules alone, whose classes must all be in the APK. */
val sharedLibraries = configurations.dependencyScope("sharedLibraries")
val sharedJars = configurations.resolvable("sharedJars") {
    extendsFrom(sharedLibraries.get())
    isTransitive = false
}

dependencies {
    coreLibraryDesugaring(libs.desugar.jdk.libs)
    for (module in sharedModules) {
        implementation("com.b3dgs.lionengine:$module:$engineVersion")
        add(sharedLibraries.name, "com.b3dgs.lionengine:$module:$engineVersion")
    }
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.junit4)
}

/**
 * Checks that an APK holds a DEX class for every class of the shared module JARs (REQ-TECH-006), and writes the
 * count per module.
 */
abstract class VerifyDex : DefaultTask() {
    /** The folder of the APK, as produced by the variant. */
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val apkFolder: DirectoryProperty

    /** The loader of the APK description of the variant. */
    @get:Internal
    abstract val loader: Property<BuiltArtifactsLoader>

    /** The shared module JARs. */
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.NAME_ONLY)
    abstract val libraries: ConfigurableFileCollection

    /** The report: one line per shared module. */
    @get:OutputFile
    abstract val report: RegularFileProperty

    @TaskAction
    fun verify() {
        val apk = loader.get().load(apkFolder.get())?.elements?.singleOrNull()?.outputFile
            ?: throw GradleException("No single APK in ${apkFolder.get()}")
        val dexed = dexClasses(File(apk))
        val lines = mutableListOf("APK: ${File(apk).name}, ${dexed.size} DEX classes")
        val missing = mutableListOf<String>()
        for (jar in libraries.files.sortedBy { it.name }) {
            val classes = jarClasses(jar)
            val absent = classes.filterNot { it in dexed }
            missing += absent.map { "${jar.name}: $it" }
            lines += "${jar.name}: ${classes.size - absent.size} of ${classes.size} classes in the DEX files"
        }
        report.get().asFile.writeText(lines.joinToString(separator = "\n", postfix = "\n"))
        lines.forEach { logger.lifecycle(it) }
        if (missing.isNotEmpty()) {
            throw GradleException("Classes missing from the DEX files of $apk:\n" + missing.joinToString("\n"))
        }
    }

    /** Returns the type descriptors of the classes of a JAR, package and module descriptors excepted. */
    private fun jarClasses(jar: File): List<String> = ZipFile(jar).use { zip ->
        zip.entries().asSequence()
            .map { it.name }
            .filter { it.endsWith(".class") && !it.startsWith("META-INF/") }
            .filterNot { it.endsWith("package-info.class") || it.endsWith("module-info.class") }
            .map { "L" + it.removeSuffix(".class") + ";" }
            .sorted()
            .toList()
    }

    /** Returns the type descriptors of the classes defined by the DEX files of an APK. */
    private fun dexClasses(apk: File): Set<String> = ZipFile(apk).use { zip ->
        val dexFiles = zip.entries().asSequence().filter { it.name.matches(Regex("classes\\d*\\.dex")) }.toList()
        if (dexFiles.isEmpty()) {
            throw GradleException("No DEX file in $apk")
        }
        dexFiles.flatMap { entry -> zip.getInputStream(entry).use { definedClasses(it.readBytes()) } }.toSet()
    }

    /**
     * Returns the type descriptors of the classes defined by one DEX file (format: source.android.com, "Dalvik
     * executable format": header, string_ids, type_ids, class_defs).
     */
    private fun definedClasses(dex: ByteArray): List<String> {
        if (String(dex, 0, 3, Charsets.US_ASCII) != "dex") {
            throw GradleException("Not a DEX file")
        }
        val buffer = ByteBuffer.wrap(dex).order(ByteOrder.LITTLE_ENDIAN)
        val stringIds = buffer.getInt(0x3C)
        val typeIds = buffer.getInt(0x44)
        val classDefsSize = buffer.getInt(0x60)
        val classDefs = buffer.getInt(0x64)
        return (0 until classDefsSize).map { index ->
            val typeIndex = buffer.getInt(classDefs + index * 32)
            val stringIndex = buffer.getInt(typeIds + typeIndex * 4)
            var start = buffer.getInt(stringIds + stringIndex * 4)
            while (dex[start].toInt() and 0x80 != 0) {
                start++
            }
            start++
            var end = start
            while (dex[end].toInt() != 0) {
                end++
            }
            String(dex, start, end - start, Charsets.UTF_8)
        }
    }
}

androidComponents {
    onVariants { variant ->
        val name = variant.name.replaceFirstChar { it.uppercase() }
        tasks.register<VerifyDex>("verify${name}Dex") {
            group = "verification"
            description = "Checks that the ${variant.name} APK holds every class of the shared modules."
            apkFolder.set(variant.artifacts.get(SingleArtifact.APK))
            loader.set(variant.artifacts.getBuiltArtifactsLoader())
            libraries.from(sharedJars)
            report.set(layout.buildDirectory.file("reports/dex/${variant.name}.txt"))
        }
    }
}
