import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

/**
 * Downloads an svg containing the overlay of Telekom's coverage map for a specific region.
 */
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

    // For the syntax see https://developers.arcgis.com/rest/services-reference/enterprise/export-image.htm
    private val downloadUrl: String
        get() = "https://t-map.telekom.de/arcgis/rest/services/public/dsl_coverage/MapServer/export?" +
                "format=svg&LANGUAGE=ger&layers=show:${Constants.ALL_LAYERS}" +
                "&bbox=${bbox.get()}" +
                "&bboxSR=${Constants.BBOX_SR}" +
                "&imageSR=${Constants.IMAGE_SR}" +
                "&size=${size.get()}" +
                "&f=image"

    init {
        group = "Breitbandausbaumonitor"
        destFile.convention(region.map { project.layout.buildDirectory.file("Breitbandausbaumonitor/coverage/${it}/latest.svg").get() })
    }

    @TaskAction
    fun download() {
        println("Download coverage map overlay for ${region.get()} to ${destFile.get().asFile.relativeTo(project.projectDir)}")
        ant.invokeMethod("get", mapOf("src" to downloadUrl, "dest" to destFile.get()))
    }

    object Constants {
        const val LAYER_DSL16 = "22,23,32,33"
        const val LAYER_VDSL50 = "20,21,30,31"
        const val LAYER_VDSL100 = "18,19,28,29"
        const val LAYER_VDSL250 = "16,26,44,45"
        const val LAYER_GLASFASER_UND_KABEL_BIS_500 = "37,38"
        const val LAYER_GLASFASER_BIS_1000 = "15,25,41,42"
        const val LAYER_HIGHSPEED_GEPLANT = "17,27"
        const val ALL_LAYERS = "$LAYER_DSL16,$LAYER_VDSL50,$LAYER_VDSL100,$LAYER_VDSL250," +
                "$LAYER_GLASFASER_UND_KABEL_BIS_500,$LAYER_GLASFASER_BIS_1000,$LAYER_HIGHSPEED_GEPLANT"

        // The spatial reference of the bbox and image parameter.
        // It's a "well-known ID", whereas 3857 is the "Web Mercator projection"
        // see https://developers.arcgis.com/documentation/common-data-types/geometry-objects.htm
        // see https://en.wikipedia.org/wiki/Web_Mercator_projection
        const val BBOX_SR = 3857
        const val IMAGE_SR = 3857
    }
}