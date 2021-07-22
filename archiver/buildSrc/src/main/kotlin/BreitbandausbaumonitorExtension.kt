import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property

interface BreitbandausbaumonitorExtension {
    val repoUri: Property<String>
    val branch: Property<String>
    val commitMessage: Property<String>
    val repoDirectory: DirectoryProperty
}