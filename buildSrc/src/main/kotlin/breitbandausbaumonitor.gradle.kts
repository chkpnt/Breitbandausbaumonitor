plugins {
    id("org.ajoberstar.git-publish")
}

val breitbandausbaumonitorExtension = project.extensions.create<BreitbandausbaumonitorExtension>("Breitbandausbaumonitor")

tasks.withType<DownloadCoverageTask> {
    val downloadTask = this
    tasks.register<UpdateCoverageTask>("update${name.removePrefix("download")}") {
        region.convention(downloadTask.region)
        bbox.convention(downloadTask.bbox)
        size.convention(downloadTask.size)
        outputDirectory.convention(layout.buildDirectory)
        doLast {
            println("huhu " + region.get())
        }
    }
}

// Legacy:

tasks.register("downloadCurrentCoverageOverlay") {
    outputs.dir("$buildDir")

    val url = "https://t-map.telekom.de/arcgis/rest/services/public/dsl_coverage/MapServer/export?format=svg&LANGUAGE=ger&layers=show:22,23,32,33,20,21,30,31,18,19,28,29,16,26,44,45,37,38,15,25,41,42,17,27&bbox=1014071.4317625333%2C6260470.589713224%2C1018676.7627168365%2C6263308.314388386&bboxSR=3857&imageSR=3857&size=1000%2C594&f=image"
    val destFile = file("$buildDir/coverage-tamm.svg")

    doLast {
        ant.invokeMethod("get", mapOf("src" to url, "dest" to destFile))
    }
}

gitPublish {
    repoUri.set(breitbandausbaumonitorExtension.repoUri)
    branch.set(breitbandausbaumonitorExtension.branch)
    commitMessage.set(breitbandausbaumonitorExtension.commitMessage)
    preserve { include("**/*") }
    contents {
        from("$buildDir/") {
            include("coverage-tamm.svg")
        }
        into(".")
    }
}