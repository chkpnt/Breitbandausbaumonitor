plugins {
    `breitbandausbaumonitor`
}

Breitbandausbaumonitor {
    repoUri.set("git@github.com:chkpnt/Breitbandausbaumonitor.git")
    branch.set("main")
    commitMessage.set("Update coverage")
}

tasks.register<DownloadCoverageTask>("downloadCoverageForTamm") {
    region.set("Tamm")
    bbox.set("1013726.978375079,6263340.8666584315,1019005.910641024,6260297.717469827")
    size.set("1105,637")
}

tasks.register<DownloadCoverageTask>("downloadCoverageForKarlsruhe") {
    region.set("Karlsruhe")
    bbox.set("920378.2575818074,6285684.365489043,952481.8094615815,6267721.663842026")
    size.set("1680,940")
}
