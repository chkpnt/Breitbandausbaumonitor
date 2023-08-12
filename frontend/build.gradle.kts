import com.github.gradle.node.npm.task.NpmTask

plugins {
    id("com.github.node-gradle.node") version "3.5.0"
    id("org.ajoberstar.git-publish")
    id("org.sonarqube") version "4.3.0.3225"
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

// Doesn't make much sense as long as the script-tags in .svelte files are not scanned :-/
sonar {
    properties {
        property("sonar.host.url", "https://sonar.chkpnt.de")
        property("sonar.login", System.getenv("SONARQUBE_TOKEN"))
        property("sonar.projectKey", "breitbandausbaumonitor-frontend")
        property("sonar.projectName", "breitbandausbaumonitor-frontend")
        property("sonar.sources", "src")
        // Mh, I don't think the html scanner is forwarding the script tags to the JS/TS-scanner
        property("sonar.html.file.suffixes", ".html,.svelte")
        // but at least the lines are counted, compared to this, which doesn't find any lines in svelte-files:
        //property("sonar.typescript.file.suffixes", ".ts,.svelte")
        property("sonar.typescript.tsconfigPath", "tsconfig.json")
    }
}
