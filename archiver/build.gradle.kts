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
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.1.0")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testImplementation("com.willowtreeapps.assertk:assertk-jvm:0.25")
    testImplementation("com.github.tomakehurst:wiremock-jre8:2.29.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
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
