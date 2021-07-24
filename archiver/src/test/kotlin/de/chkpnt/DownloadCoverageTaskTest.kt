package de.chkpnt

import assertk.assertThat
import assertk.assertions.exists
import assertk.assertions.isEqualTo
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.net.URI
import java.nio.file.Path
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.readText

@ExperimentalPathApi
internal class DownloadCoverageTaskTest {
    private lateinit var sut: DownloadCoverageTask
    private lateinit var newRegionOutputDir: Path
    private lateinit var existingRegionOutputDir: Path

    private lateinit var server: WireMockServer

    @TempDir
    lateinit var tempDir: Path

    @BeforeEach
    fun setup() {
        server = WireMockServer(0)
        server.start()

        val project = ProjectBuilder.builder().build()
        sut = project.tasks.create("downloadCoverage", DownloadCoverageTask::class.java)
        sut.endpoint.set(URI.create("http://localhost:${server.port()}"))
    }

    @AfterEach
    fun tearDown() {
        server.resetRequests()
        server.resetToDefaultMappings()
        server.stop()
    }

    @Test
    fun `test download`() {
        server.stubFor(get(anyUrl()).willReturn(ok().withBody("<svg></svg>")))

        sut.region.set("Testdorf")
        sut.bbox.set("1000000.01,6000000.01,1002000.01,6002000.01")
        sut.size.set("1000,600")
        sut.destFile.set(tempDir.resolve("download/latest.svg").toFile())

        sut.download()

        assertThat(tempDir.resolve("download/latest.svg")).exists()
        assertThat(tempDir.resolve("download/latest.svg").readText())
            .isEqualTo("<svg></svg>")
        server.verify(getRequestedFor(urlEqualTo("/arcgis/rest/services/public/dsl_coverage/MapServer/export?" +
                "format=svg&LANGUAGE=ger" +
                "&layers=show:22,23,32,33,20,21,30,31,18,19,28,29,16,26,44,45,37,38,15,25,41,42,17,27" +
                "&bbox=1000000.01,6000000.01,1002000.01,6002000.01&bboxSR=3857&imageSR=3857" +
                "&size=1000,600" +
                "&transparent=true" +
                "&f=image")))
    }
}
