plugins {
    `kotlin-dsl`
    kotlin("plugin.serialization") version embeddedKotlinVersion
    jacoco
    id("org.sonarqube") version "4.3.0.3225"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.ajoberstar.git-publish:gradle-git-publish:3.0.1")
    // Attention: kotlinx-serialization-json must be compatible with embeddedKotlinVersion
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")

    testImplementation(platform("org.junit:junit-bom:6.1.3"))
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:6.1.3")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    testImplementation("com.willowtreeapps.assertk:assertk-jvm:0.28.1")
    testImplementation("com.github.tomakehurst:wiremock-jre8:3.0.1")
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

sonar {
    properties {
        property("sonar.host.url", "https://sonar.chkpnt.de")
        property("sonar.login", System.getenv("SONARQUBE_TOKEN"))
        property("sonar.coverage.jacoco.xmlReportPaths", "build/reports/jacoco/test/jacocoTestReport.xml")
    }
}

