/*
 * Android/DEX verification build (ADR-0012, ADR-0044, docs/features/build-skeleton.md item 4,
 * docs/android-compatibility.md).
 *
 * It consumes the shared modules installed in the local Maven repository by `mvn -B install` (run from the
 * repository root first) and proves that they dex and run on API 21:
 * - api-lint: compiles the shared modules' sources against android.jar and runs the lint check NewApi as an error;
 * - verification: an application that depends on the installed shared modules, so that D8 (debug) and R8 (release)
 *   process them; its instrumentation tests run on an API 21 emulator.
 *
 * Shared modules: the modules of the aggregator pom.xml compiled at the shared level (no jdk.version override,
 * ADR-0044), except lionengine-test-support (test scope only, never shipped). Desktop-only modules set
 * jdk.version 25 and are left out, so a new module is covered as soon as the aggregator declares it.
 */
import javax.xml.parsers.DocumentBuilderFactory
import org.w3c.dom.Element

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        // The engine modules installed by `mvn -B install`; nothing else is read from the local repository.
        mavenLocal {
            content {
                includeGroup("com.b3dgs.lionengine")
            }
        }
        // The parent POM b3dgs-parent, a snapshot (ADR-0051), as declared by the aggregator pom.xml.
        maven("https://central.sonatype.com/repository/maven-snapshots") {
            mavenContent {
                snapshotsOnly()
                includeGroup("com.b3dgs")
            }
        }
        google()
        mavenCentral()
    }
}

/** Module left out of the shared modules: the test kit is used in test scope only and never shipped. */
val testKit = "lionengine-test-support"

/**
 * Returns the direct child elements of an element with the given name.
 */
fun children(parent: Element, name: String): List<Element> {
    val nodes = parent.childNodes
    return (0 until nodes.length).map { nodes.item(it) }.filterIsInstance<Element>().filter { it.tagName == name }
}

/**
 * Returns the text of the only direct child with the given name, or null.
 */
fun childText(parent: Element, name: String): String? = children(parent, name).singleOrNull()?.textContent?.trim()

/**
 * Reads a POM file.
 */
fun readPom(file: File): Element =
    DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file).documentElement

val repositoryRoot: File = rootDir.parentFile
val aggregator: Element = readPom(repositoryRoot.resolve("pom.xml"))
val engineVersion: String = checkNotNull(childText(aggregator, "version")) { "No version in the aggregator pom.xml" }
val modules: List<String> = children(aggregator, "modules").flatMap { children(it, "module") }.map { it.textContent.trim() }
val sharedModules: List<String> = modules.filter { module ->
    val properties = children(readPom(repositoryRoot.resolve("$module/pom.xml")), "properties")
    module != testKit && properties.none { childText(it, "jdk.version") != null }
}
check(sharedModules.isNotEmpty()) { "No shared module found in the aggregator pom.xml" }

gradle.extra["lionengine.repositoryRoot"] = repositoryRoot
gradle.extra["lionengine.version"] = engineVersion
gradle.extra["lionengine.sharedModules"] = sharedModules

rootProject.name = "lionengine-android"
include(":api-lint", ":verification")
