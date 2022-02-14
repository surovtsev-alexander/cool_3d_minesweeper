import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.initialization.dsl.ScriptHandler

object ConfigurationNames {
    const val classpath = ScriptHandler.CLASSPATH_CONFIGURATION
    const val debugImplementation = "debugImplementation"
    const val implementation = "implementation"
    const val kapt = "kapt"
    const val testImplementation = "testImplementation"
    const val androidTestImplementation = "androidTestImplementation"
}

fun DependencyHandler.topLevelDependencies() {
    add(ConfigurationNames.classpath,
        "com.android.tools.build:gradle:${Versions.gradle}")
    add(ConfigurationNames.classpath,
        "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.Kotlin.kotlin}")
}

fun DependencyHandler.daggerDependencies() {
    add(ConfigurationNames.implementation,
        "com.google.dagger:dagger:${Versions.dagger}")
    add(ConfigurationNames.kapt,
        "com.google.dagger:dagger-compiler:${Versions.dagger}")
}

fun DependencyHandler.logcatDependency() {
    add(ConfigurationNames.implementation,
        "com.squareup.logcat:logcat:${Versions.Utils.logcat}")
}

fun DependencyHandler.roomDependencies() {
    add(ConfigurationNames.implementation,
        "androidx.room:room-ktx:${Versions.AndroidX.room}")
    add(ConfigurationNames.kapt,
        "androidx.room:room-compiler:${Versions.AndroidX.room}")
}

fun DependencyHandler.threetenabpDependency() {
    add(ConfigurationNames.implementation,
        "com.jakewharton.threetenabp:threetenabp:${Versions.Utils.threetenabp}")
}

fun DependencyHandler.glmDependency() {
    add(ConfigurationNames.implementation,
        "com.github.kotlin-graphics:glm:${Versions.Utils.glm}")
}

fun DependencyHandler.gsonDependency() {
    add(ConfigurationNames.implementation,
        "com.google.code.gson:gson:${Versions.Google.gson}")
}

fun DependencyHandler.composeNavigationDependency() {
    add(ConfigurationNames.implementation,
        "androidx.navigation:navigation-compose:${Versions.AndroidX.composeNavigation}")
}

fun DependencyHandler.coroutinesDependency() {
    add(ConfigurationNames.implementation,
        "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.Kotlin.coroutines}")
}

fun DependencyHandler.commonComposeDependencies() {
    add(ConfigurationNames.implementation,
        "androidx.compose.ui:ui:${Versions.AndroidX.compose}")
    add(ConfigurationNames.implementation,
        "androidx.compose.material:material:${Versions.AndroidX.compose}")
    add(ConfigurationNames.implementation,
        "androidx.compose.runtime:runtime-livedata:${Versions.AndroidX.compose}")
}

fun DependencyHandler.navigationDependencies() {
    add(ConfigurationNames.implementation,
        "androidx.navigation:navigation-fragment-ktx:${Versions.AndroidX.navigation}")
    add(ConfigurationNames.implementation,
        "androidx.navigation:navigation-ui-ktx:${Versions.AndroidX.navigation}")
}

fun DependencyHandler.testDependencies() {
    add(ConfigurationNames.testImplementation,
        "junit:junit:${Versions.JUnit.junit}")
    add(ConfigurationNames.androidTestImplementation,
        "androidx.test.ext:junit:${Versions.JUnit.junitExt}")
    add(ConfigurationNames.androidTestImplementation,
        "androidx.test.espresso:espresso-core:${Versions.AndroidX.espresso}")
}

fun DependencyHandler.defaultDependencies() {
    add(ConfigurationNames.implementation,
        "androidx.core:core-ktx:${Versions.AndroidX.coreCtx}")
    add(ConfigurationNames.implementation,
        "androidx.appcompat:appcompat:${Versions.AndroidX.appCompat}")
    add(ConfigurationNames.implementation,
        "com.google.android.material:material:${Versions.AndroidX.material}")
}

fun DependencyHandler.navigationAnimationDependency() {
    add(ConfigurationNames.implementation,
        "com.google.accompanist:accompanist-navigation-animation:${Versions.Utils.navigationAnimation}")
}

fun DependencyHandler.splashScreenDependency() {
    add(ConfigurationNames.implementation,
        "androidx.core:core-splashscreen:${Versions.AndroidX.splashScreen}")
}

fun DependencyHandler.leakCanaryDependency() {
    add(ConfigurationNames.debugImplementation,
        "com.squareup.leakcanary:leakcanary-android:${Versions.Utils.leakCanary}")

}

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
