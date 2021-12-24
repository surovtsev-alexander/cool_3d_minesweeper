import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.initialization.dsl.ScriptHandler

object ConfigurationNames {
    const val classpath = ScriptHandler.CLASSPATH_CONFIGURATION
    const val implementation = "implementation"
    const val kapt = "kapt"
    const val testImplementation = "testImplementation"
    const val androidTestImplementation = "androidTestImplementation"
}

fun DependencyHandler.topLevelDependencies() {
    add(ConfigurationNames.classpath, "com.android.tools.build:gradle:7.0.4")
    add(ConfigurationNames.classpath, "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}")
}

fun DependencyHandler.daggerDependencies() {
    add(ConfigurationNames.implementation, "com.google.dagger:dagger:${Versions.dagger}")
    add(ConfigurationNames.kapt, "com.google.dagger:dagger-compiler:${Versions.dagger}")
}

fun DependencyHandler.logcatDependency() {
    add(ConfigurationNames.implementation, "com.squareup.logcat:logcat:0.1")
}

fun DependencyHandler.roomDependencies() {
    add(ConfigurationNames.implementation,"androidx.room:room-ktx:${Versions.room}")
    add(ConfigurationNames.kapt,"androidx.room:room-compiler:${Versions.room}")
}

fun DependencyHandler.threetenabpDependency() {
    add(ConfigurationNames.implementation, "com.jakewharton.threetenabp:threetenabp:1.3.1")
}

fun DependencyHandler.glmDependency() {
    add(ConfigurationNames.implementation, "com.github.kotlin-graphics:glm:v1.0.1")
}

fun DependencyHandler.gsonDependency() {
    add(ConfigurationNames.implementation, "com.google.code.gson:gson:2.8.9")
}

fun DependencyHandler.composeNavigationDependency() {
    add(ConfigurationNames.implementation, "androidx.navigation:navigation-compose:${Versions.composeNavigation}")
}

fun DependencyHandler.coroutinesDependency() {
    add(ConfigurationNames.implementation, "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0-RC2")
}

fun DependencyHandler.commonComposeDependencies() {
    add(ConfigurationNames.implementation, "androidx.compose.ui:ui:${Versions.compose}")
    add(ConfigurationNames.implementation, "androidx.compose.material:material:${Versions.compose}")
    add(ConfigurationNames.implementation, "androidx.compose.runtime:runtime-livedata:${Versions.compose}")
}

fun DependencyHandler.kotlinNavigationDependencies() {
    add(ConfigurationNames.implementation, "androidx.navigation:navigation-fragment-ktx:${Versions.nav}")
    add(ConfigurationNames.implementation, "androidx.navigation:navigation-ui-ktx:${Versions.nav}")
}

fun DependencyHandler.testDependencies() {
    add(ConfigurationNames.testImplementation, "junit:junit:4.13.2")
    add(ConfigurationNames.androidTestImplementation,"androidx.test.ext:junit:1.1.3")
    add(ConfigurationNames.androidTestImplementation, "androidx.test.espresso:espresso-core:3.4.0")
}

fun DependencyHandler.defaultDependencies() {
    add(ConfigurationNames.implementation, "androidx.core:core-ktx:1.7.0")
    add(ConfigurationNames.implementation, "androidx.appcompat:appcompat:1.4.0")
    add(ConfigurationNames.implementation, "com.google.android.material:material:1.4.0")
}
