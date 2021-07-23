import com.github.gradle.node.npm.task.NpmTask

plugins {
    id("com.github.node-gradle.node") version "3.1.0"
    id("org.ajoberstar.git-publish")
}

node {
    npmInstallCommand.set("ci")
}

tasks.register<NpmTask>("buildFrontend") {
    npmCommand.set(listOf("run", "build"))
    dependsOn("npmInstall")
}

gitPublish {
    repoUri.set("git@github.com:chkpnt/Breitbandausbaumonitor.git")
    branch.set("gh-pages")
    commitMessage.set("Update front end")
    contents {
        from(layout.projectDirectory.dir("dist"))
    }
}
