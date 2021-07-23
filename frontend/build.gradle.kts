import com.github.gradle.node.npm.task.NpmTask

plugins {
    id("com.github.node-gradle.node") version "3.1.0"
}

node {
    npmInstallCommand.set("ci")
}

tasks.register<NpmTask>("buildFrontend") {
    npmCommand.set(listOf("run", "build"))
    dependsOn("npmInstall")
}
