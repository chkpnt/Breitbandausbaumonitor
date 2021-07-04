import org.ajoberstar.gradle.git.publish.tasks.GitPublishCommit
import org.ajoberstar.gradle.git.publish.tasks.GitPublishPush
import org.ajoberstar.gradle.git.publish.tasks.GitPublishReset
import org.ajoberstar.grgit.Grgit


val extension = project.extensions.create<BreitbandausbaumonitorExtension>("Breitbandausbaumonitor")
extension.repoDirectory.convention(project.layout.buildDirectory.dir("Breitbandausbaumonitor/repo"))

tasks.withType<DownloadCoverageTask> {
    val downloadTask = this
    tasks.register<UpdateCoverageTask>("update${name.removePrefix("download")}") {
        region.convention(downloadTask.region)
        bbox.convention(downloadTask.bbox)
        size.convention(downloadTask.size)
        coverageFile.convention(downloadTask.destFile)
        outputDirectory.convention(downloadTask.region.map { extension.repoDirectory.dir("regions/${it}/coverage").get() })
        dependsOn(repoCheckoutTask)
    }
}

tasks.register("updateAllCoverageMaps") {
    group = "Breitbandausbaumonitor"
    description = "Update all coverage overlays in a local clone of the Breitbandausbaumonitor repository"

    dependsOn(tasks.withType<UpdateCoverageTask>())
}

val repoCheckoutTask = tasks.register<GitPublishReset>("repoCheckout") {
    group = "Breitbandausbaumonitor"
    description = "Checkout ${extension.repoUri}"
    repoDirectory.set(extension.repoDirectory)
    repoUri.set(extension.repoUri)
    branch.set(extension.branch)

    preserve = PatternSet()
    preserve.include("**/*")
}

tasks.register<GitPublishCommit>("repoCommit") {
    group = "Breitbandausbaumonitor"
    description = "Commit changes to be published to ${extension.repoUri}"
    message.set(extension.commitMessage)

    // I do not use Grgit from GitPublishReset-task as compared how it's done in the not-applied GitPublishPlugin,
    // as I want be able to commit without resetting the repository in advance
    val git = Grgit.open { dir = extension.repoDirectory.get().asFile }
    grgit.set(git)
}

tasks.register<GitPublishPush>("repoPush") {
    group = "Breitbandausbaumonitor"
    description = "Push changes from the local Breitbandausbaumonitor repository to ${extension.repoUri}"
    val git = Grgit.open { dir = extension.repoDirectory.get().asFile }
    grgit.set(git)
    branch.set(extension.branch)
}
