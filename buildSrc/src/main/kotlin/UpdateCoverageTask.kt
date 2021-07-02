import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

abstract class UpdateCoverageTask : DefaultTask() {

    @get:Input
    abstract val region: Property<String>

    @get:Input
    abstract val bbox: Property<String>

    @get:Input
    abstract val size: Property<String>

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    private val emptyMetadata: String = """
        {
            "bbox": null,
            "size": null,
            "coverages": [
            ]
        }
    """.trimIndent()

    @TaskAction
    fun update() {

        updateMetadata()
        //ant.invokeMethod("get", mapOf("src" to downloadUrl, "dest" to destFile.get()))
    }

    private fun updateMetadata() {
        var metadataFile = outputDirectory.file("coverage/data.json").get().asFile
        val metadataJson = metadataFile.takeIf { it.exists() }?.readText() ?: emptyMetadata
        val metadata = (groovy.json.JsonSlurper().parseText(metadataJson) as Map<String, *>).toMutableMap()
        metadata["bbox"] = bbox.get();
        //metadata = bbox.get()
        //val a = groovy.json.JsonSlurper().parse(metadataFile.asFile)
        println(metadata)
        println(metadata.javaClass.name)
    }
}
