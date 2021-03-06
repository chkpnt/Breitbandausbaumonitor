plugins {
    `kotlin-dsl`
    kotlin("plugin.serialization") version embeddedKotlinVersion
    jacoco
    id("org.sonarqube") version "3.3"
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("org.ajoberstar:gradle-git-publish:3.0.0")
    // Attention: kotlinx-serialization-json must be compatible with embeddedKotlinVersion
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.2")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testImplementation("com.willowtreeapps.assertk:assertk-jvm:0.25")
    testImplementation("com.github.tomakehurst:wiremock-jre8:2.32.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
}

tasks.register("printEmbeddedKotlinVersion") {
    doLast {
        println(embeddedKotlinVersion)
    }
}

tasks {
    test {
        useJUnitPlatform()
        finalizedBy(jacocoTestReport)
    }
    jacocoTestReport {
        reports {
            xml.required.set(true)
            html.required.set(true)
        }
    }
}

sonarqube {
    properties {
        property("sonar.host.url", "https://sonar.chkpnt.de")
        property("sonar.login", System.getenv("SONARQUBE_TOKEN"))
        property("sonar.coverage.jacoco.xmlReportPaths", "build/reports/jacoco/test/jacocoTestReport.xml")
    }
}

