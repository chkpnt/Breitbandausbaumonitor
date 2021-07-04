import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

abstract class DownloadCoverageTask : DefaultTask() {

    @get:Input
    abstract val region: Property<String>

    @get:Input
    abstract val bbox: Property<String>

    @get:Input
    abstract val size: Property<String>

    @get:OutputFile
    abstract val destFile: RegularFileProperty

    val checkOncePerHour: LocalDateTime
        @Input
        get() = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS)

    // see https://developers.arcgis.com/rest/services-reference/enterprise/export-image.htm
    private val downloadUrl: String
        get() = "https://t-map.telekom.de/arcgis/rest/services/public/dsl_coverage/MapServer/export?" +
                "format=svg&LANGUAGE=ger&layers=show:22,23,32,33,20,21,30,31,18,19,28,29,16,26,44,45,37,38,15,25,41,42,17,27" +
                "&bbox=${bbox.get()}" +
                "&bboxSR=${bboxSR}" +
                "&imageSR=${imageSR}" +
                "&size=${size.get()}" +
                "&f=image";

    // The spatial reference of the bbox and image parameter.
    // It's a "well-known ID", whereas 3857 is the "Web Mercator projection"
    // see https://developers.arcgis.com/documentation/common-data-types/geometry-objects.htm
    // see https://en.wikipedia.org/wiki/Web_Mercator_projection
    private val bboxSR = 3857
    private val imageSR = 3857

    init {
        destFile.convention(region.map { project.layout.buildDirectory.file("coverage/${it}/latest.svg").get() })
    }

    @TaskAction
    fun download() {
        println("Region ${region.get()}")
        println("destFile ${destFile.get()}")

       // ant.invokeMethod("get", mapOf("src" to downloadUrl, "dest" to destFile.get()))
    }
}