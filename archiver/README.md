# Breitbandausmonitor (Gradle Plugin)

This [Gradle](https://gradle.org/) plugin written in [Kotlin](https://kotlinlang.org/) and [Gradle Kotlin DSL](https://docs.gradle.org/current/userguide/kotlin_dsl.html)
is used by the project [Breitbandausbaumonitor](https://github.com/chkpnt/Breitbandausbaumonitor/).

The plugin registers an extension `Breitbandausbaumonitor` which is used to configure the repository in which the overlays are archived:
```kotlin
Breitbandausbaumonitor {
    repoUri.set("git@github.com:chkpnt/Breitbandausbaumonitor.git")
    branch.set("main")
    commitMessage.set("Update coverage")
    archiveDirectory.set("overlays")
}
```

The plugin registers the following self-describing tasks:
- `repoCheckout`
- `repoCommit`
- `repoPush`
- `updateAllCoverageMaps`

The plugin provides tasks of the type `DownloadCoverageTask` 
and `UpdateCoverageTask`.
The intended use is that the user register an instance of `DownloadCoverageTask`, which describes the region, bounding box and size of the coverage map:
```kotlin
tasks.register<DownloadCoverageTask>("downloadCoverageForTamm") {
    region.set("Tamm")
    bbox.set("1014071.4317625333,6260470.589713224,1018676.7627168365,6263308.314388386")
    size.set("1000,594")
}
```
For each `DownloadCoverageTask`, the plugin registers a corresponding `UpdateCoverageTask`, which is used to detect if the downloaded map differs from the last archived one, and if so, copies the map into the locally cloned repository and updates the metadata file.

In order to update all coverage overlay locally, just run
```console
$ ./gradlew updateAllCoverageMaps
```
Afterwards, the updated (but not yet commited nor pushed) repository is available at `build/Breitbandausbaumonitor/repo/`.