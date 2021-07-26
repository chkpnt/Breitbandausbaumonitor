package de.chkpnt

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import java.time.OffsetDateTime
import kotlin.io.path.*

@ExperimentalPathApi
internal class UpdateCoverageTaskTest {
    private lateinit var sut: UpdateCoverageTask
    private lateinit var newRegionOutputDir: Path
    private lateinit var existingRegionOutputDir: Path

    @TempDir
    lateinit var tempDir: Path

    @BeforeEach
    fun setup() {
        tempDir.resolve("download").createDirectory()
            .resolve("latest.svg").writeText("<svg>new</svg>")

        newRegionOutputDir = tempDir.resolve("overlays/empty").createDirectories()

        existingRegionOutputDir = tempDir.resolve("overlays/existing").createDirectories()
        tempDir.resolve("overlays/existing/2021-01-01.svg").writeText("<svg>old</svg>")
        tempDir.resolve("overlays/existing/latest").writeText("<svg>old</svg>")
        tempDir.resolve("overlays/existing/data.json").writeText(EXISTING_METADATA_JSON)

        val project = ProjectBuilder.builder().build()
        sut = project.tasks.create("updateCoverage", UpdateCoverageTask::class.java)
    }

    @Test
    fun `test task for new region`() {
        sut.region.set("Testdorf")
        sut.bbox.set("1000000.01,6000000.01,1002000.01,6002000.01")
        sut.size.set("1000,600")
        sut.coverageFile.set(tempDir.resolve("download/latest.svg").toFile())
        sut.outputDirectory.set(newRegionOutputDir.toFile())
        sut.timestamp.set(OffsetDateTime.parse("2021-07-24T12:00:30.1234+02:00"))

        sut.updateIfNecessary()

        assertThat(newRegionOutputDir.resolve("data.json").readText()).isEqualTo(
            """
            {
                "coverages": [
                    {
                        "timestamp": "2021-07-24T12:00:30+02:00",
                        "file": "2021-07-24.svg",
                        "sha1": "175c6d6e4de2a2e01413c1e344f948ef86d9e286",
                        "bbox": "1000000.01,6000000.01,1002000.01,6002000.01",
                        "size": "1000,600"
                    }
                ]
            }
            """.trimIndent()
        )
        assertThat(newRegionOutputDir.resolve("2021-07-24.svg").readText())
            .isEqualTo("<svg>new</svg>")
        assertThat(newRegionOutputDir.resolve("latest.svg").readText())
            .isEqualTo("<svg>new</svg>")
    }

    @Test
    fun `test task for existing region`() {
        sut.region.set("Testdorf")
        sut.bbox.set("1000000.01,6000000.01,1002000.01,6002000.01")
        sut.size.set("1000,600")
        sut.coverageFile.set(tempDir.resolve("download/latest.svg").toFile())
        sut.outputDirectory.set(existingRegionOutputDir.toFile())
        sut.timestamp.set(OffsetDateTime.parse("2021-07-24T12:00:30.1234+02:00"))

        sut.updateIfNecessary()

        assertThat(existingRegionOutputDir.resolve("data.json").readText()).isEqualTo(
            """
            {
                "coverages": [
                    {
                        "timestamp": "2021-07-24T12:00:30+02:00",
                        "file": "2021-07-24.svg",
                        "sha1": "175c6d6e4de2a2e01413c1e344f948ef86d9e286",
                        "bbox": "1000000.01,6000000.01,1002000.01,6002000.01",
                        "size": "1000,600"
                    },
                    {
                        "timestamp": "2021-01-01T19:00:30+02:00",
                        "file": "2021-01-01.svg",
                        "sha1": "f1ab400082758f5cff416651d2c84b48db8c8c4",
                        "bbox": "1000000.01,6000000.01,1002000.01,6002000.01",
                        "size": "1000,600",
                        "comment": "first map"
                    }
                ]
            }
            """.trimIndent()
        )
        assertThat(existingRegionOutputDir.resolve("2021-07-24.svg").readText())
            .isEqualTo("<svg>new</svg>")
        assertThat(existingRegionOutputDir.resolve("latest.svg").readText())
            .isEqualTo("<svg>new</svg>")
    }

    companion object {
        val EXISTING_METADATA_JSON =
            """
            {
                "coverages": [
                    {
                        "timestamp": "2021-01-01T19:00:30+02:00",
                        "file": "2021-01-01.svg",
                        "sha1": "f1ab400082758f5cff416651d2c84b48db8c8c4",
                        "bbox": "1000000.01,6000000.01,1002000.01,6002000.01",
                        "size": "1000,600",
                        "comment": "first map"
                    }
                ]
            }
            """.trimIndent()
    }
}
