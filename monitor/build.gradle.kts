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
    bbox.set("1014071.4317625333,6260470.589713224,1018676.7627168365,6263308.314388386")
    size.set("1000,594")
}

tasks.register<DownloadCoverageTask>("downloadCoverageForKarlsruhe") {
    region.set("Karlsruhe")
    bbox.set("920378.2575818074,6285684.365489043,952481.8094615815,6267721.663842026")
    size.set("1680,940")
}
