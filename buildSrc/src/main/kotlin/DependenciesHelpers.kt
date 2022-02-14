import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.initialization.dsl.ScriptHandler

fun DependencyHandler.includeProjectModules(
    modules: Array<String>
) {
    modules.map {
        includeProjectModule(it)
    }
}

fun DependencyHandler.includeProjectModule(
    module: String
) {
    add(
        ConfigurationNames.implementation,
        project(mapOf("path" to module))
    )
}

object ConfigurationNames {
    const val androidTestImplementation = "androidTestImplementation"
    const val classpath = ScriptHandler.CLASSPATH_CONFIGURATION
    const val debugImplementation = "debugImplementation"
    const val implementation = "implementation"
    const val kapt = "kapt"
    const val testImplementation = "testImplementation"
}

fun DependencyHandler.androidTestImplementation(
    dependencyNotation: String
) {
    add(ConfigurationNames.androidTestImplementation, dependencyNotation)
}

fun DependencyHandler.classpath(
    dependencyNotation: String
) {
    add(ConfigurationNames.classpath, dependencyNotation)
}

fun DependencyHandler.debugImplementation(
    dependencyNotation: String
) {
    add(ConfigurationNames.debugImplementation, dependencyNotation)
}

fun DependencyHandler.implementation(
    dependencyNotation: String
) {
    add(ConfigurationNames.implementation, dependencyNotation)
}

fun DependencyHandler.kapt(
    dependencyNotation: String
) {
    add(ConfigurationNames.kapt, dependencyNotation)
}

fun DependencyHandler.testImplementation(
    dependencyNotation: String
) {
    add(ConfigurationNames.testImplementation, dependencyNotation)
}
