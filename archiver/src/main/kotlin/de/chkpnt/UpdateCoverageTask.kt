@file:UseSerializers(OffsetDateTimeIso8601Serializer::class)

package de.chkpnt
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.*
import org.gradle.internal.hash.Hashing
import java.io.File
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

/**
 * Compares the downloaded svg with the latest from the corresponding folder in the
 * Breitbandausbaumonitor repository. If it differs, the file is copied to the
 * repository and the metadata file data.json is updated.
 */
abstract class UpdateCoverageTask : DefaultTask() {

    @get:Input
    abstract val region: Property<String>

    @get:Input
    abstract val bbox: Property<String>

    @get:Input
    abstract val size: Property<String>

    @get:InputFile
    abstract val coverageFile: RegularFileProperty

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    @get:Internal
    internal abstract val timestamp: Property<OffsetDateTime>

    private val metadataFile: File
        get() = outputDirectory.file("data.json").get().asFile

    init {
        group = "Breitbandausbaumonitor"
        timestamp.convention(project.providers.provider { OffsetDateTime.now() })
    }

    @TaskAction
    fun updateIfNecessary() {
        val coveragefileMetadata = createCoveragefileMetadata()
        val previousRegionMetadata = readPreviousRegionMetadata()
        if (!hasCoverageChanged(previousRegionMetadata, coveragefileMetadata)) {
            println("No change in coverage map overlay detected")
            return
        }
        update(coveragefileMetadata, previousRegionMetadata)
    }

    private fun update(coveragefileMetadata: CoveragefileMetadata, previousRegionMetadata: RegionMetadata) {
        val today = timestamp.get().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val newFileName = "${today}.${coverageFile.get().asFile.extension}"
        val newFile = outputDirectory.file(newFileName).get().asFile
        coverageFile.get().asFile.copyTo(newFile, overwrite = true)
        println("New coverage map overlay saved to ${newFile.relativeTo(project.projectDir)}")

        // copy, not symblink, to allow better diffs in the repository
        val latest = outputDirectory.file("latest.svg").get().asFile
        coverageFile.get().asFile.copyTo(latest, overwrite = true)

        coveragefileMetadata.file = newFileName
        previousRegionMetadata.coverages.add(0, coveragefileMetadata)
        metadataFile.writeText(json.encodeToString(previousRegionMetadata))
    }

    private fun hasCoverageChanged(regionMetadata: RegionMetadata, newCoveragefileMetadata: CoveragefileMetadata): Boolean {
        val lastHash = regionMetadata.coverages.firstOrNull()?.sha1
        val currentHash = newCoveragefileMetadata.sha1
        return lastHash != currentHash
    }

    private fun readPreviousRegionMetadata(): RegionMetadata {
        return metadataFile.takeIf { it.exists() }
                ?.readText()
                ?.let { json.decodeFromString<RegionMetadata>(it) }
                ?: RegionMetadata()
    }

    private fun createCoveragefileMetadata(): CoveragefileMetadata {
        val file = coverageFile.get().asFile
        return CoveragefileMetadata(
                timestamp = timestamp.get().truncatedTo(ChronoUnit.SECONDS),
                file = file.name,
                sha1 = Hashing.sha1().hashFile(file).toString(),
                bbox = bbox.get(),
                size = size.get(),
        )
    }

    private val json = Json { prettyPrint = true }
}

@Serializable
data class CoveragefileMetadata(
        val timestamp: OffsetDateTime,
        var file: String,
        val sha1: String,
        val bbox: String,
        val size: String,
        val comment: String? = null,
)

@Serializable
data class RegionMetadata(
        val coverages: MutableList<CoveragefileMetadata> = mutableListOf(),
)


object OffsetDateTimeIso8601Serializer: KSerializer<OffsetDateTime> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("OffsetDateTime", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): OffsetDateTime = OffsetDateTime.parse(decoder.decodeString())

    override fun serialize(encoder: Encoder, value: OffsetDateTime) = encoder.encodeString(value.toString())

}
