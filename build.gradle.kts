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

