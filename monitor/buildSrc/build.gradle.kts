plugins {
    `kotlin-dsl`
    kotlin("plugin.serialization") version embeddedKotlinVersion
}


repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("org.ajoberstar:gradle-git-publish:3.0.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.1.0")
}