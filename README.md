# Breitbandausbaumonitor
[![License](https://img.shields.io/github/license/chkpnt/Breitbandausbau.svg?label=License)](https://tldrlegal.com/license/apache-license-2.0-(apache-2.0)) 
[![auto-updating coverage](https://github.com/chkpnt/Breitbandausbaumonitor/actions/workflows/update-coverage.yml/badge.svg)](https://github.com/chkpnt/Breitbandausbaumonitor/actions/workflows/update-coverage.yml)

This project is used to archive the current overlay for landline data transmission technologies of Deutsche Telekom's [coverage map] in specific regions on a daily basis:

- Tamm: since March 14, 2021
- Karlsruhe: since Juli 04, 2021

## Current status
### Tamm
![Ausbaukarte Tamm](overlays/Tamm/latest.svg)

### Karlsruhe
![Ausbaukarte Karlsruhe](overlays/Karlsruhe/latest.svg)

### Legend
![Legende](.github/images/Telekom-Legende.png)

## Archiving other regions
Just open a pull request with a new `DownloadCoverageTask` in [build.gradle.kts](build.gradle.kts).

For example, the following task is used to fetch the coverage overlay for _Karlsruhe_:
```kotlin
tasks.register<DownloadCoverageTask>("downloadCoverageForKarlsruhe") {
    region.set("Karlsruhe")
    bbox.set("920378.2575818074,6285684.365489043,952481.8094615815,6267721.663842026")
    size.set("1680,940")
}
```

The required parameters for `bbox` (_Bounding Box_) and `size` can be determined from the corresponding request when viewing the 
the [coverage map]:

![Howto](.github/images/howto.png)

Please do not try to cover too large areas, as the file size of the downloaded SVG grows quickly.

## License

For the source code within this repository: Apache-2.0

(of course not for the archived SVGs and the legend)

[coverage map]: https://t-map.telekom.de/tmap2/coverage_checker/?initLayerGroup=fixedline&initLayerIds=coverage5G,coverageVDSL50,coverageVDSL100,coverageVDSL250,coverageGlasfaser1000,coveragePlanned