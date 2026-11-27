// Android/DEX verification build: see settings.gradle.kts (ADR-0012, ADR-0044).
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
}
