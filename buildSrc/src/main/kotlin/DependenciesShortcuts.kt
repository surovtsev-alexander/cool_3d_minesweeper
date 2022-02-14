import org.gradle.api.artifacts.dsl.DependencyHandler

fun DependencyHandler.topLevelDependencies() {
    classpath("com.android.tools.build:gradle:${Versions.gradle}")
    classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.Kotlin.kotlin}")
}

fun DependencyHandler.daggerDependencies() {
    implementation("com.google.dagger:dagger:${Versions.dagger}")
    kapt("com.google.dagger:dagger-compiler:${Versions.dagger}")
}

fun DependencyHandler.logcatDependency() {
    implementation("com.squareup.logcat:logcat:${Versions.Utils.logcat}")
}

fun DependencyHandler.roomDependencies() {
    implementation("androidx.room:room-ktx:${Versions.AndroidX.room}")
    kapt("androidx.room:room-compiler:${Versions.AndroidX.room}")
}

fun DependencyHandler.threetenabpDependency() {
    implementation("com.jakewharton.threetenabp:threetenabp:${Versions.Utils.threetenabp}")
}

fun DependencyHandler.glmDependency() {
    implementation("com.github.kotlin-graphics:glm:${Versions.Utils.glm}")
}

fun DependencyHandler.gsonDependency() {
    implementation("com.google.code.gson:gson:${Versions.Google.gson}")
}

fun DependencyHandler.composeNavigationDependency() {
    implementation("androidx.navigation:navigation-compose:${Versions.AndroidX.composeNavigation}")
}

fun DependencyHandler.coroutinesDependency() {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.Kotlin.coroutines}")
}

fun DependencyHandler.commonComposeDependencies() {
    implementation("androidx.compose.ui:ui:${Versions.AndroidX.compose}")
    implementation("androidx.compose.material:material:${Versions.AndroidX.compose}")
    implementation("androidx.compose.runtime:runtime-livedata:${Versions.AndroidX.compose}")
}

fun DependencyHandler.navigationDependencies() {
    implementation("androidx.navigation:navigation-fragment-ktx:${Versions.AndroidX.navigation}")
    implementation("androidx.navigation:navigation-ui-ktx:${Versions.AndroidX.navigation}")
}

fun DependencyHandler.testDependencies() {
    testImplementation("junit:junit:${Versions.JUnit.junit}")
    androidTestImplementation("androidx.test.ext:junit:${Versions.JUnit.junitExt}")
    androidTestImplementation("androidx.test.espresso:espresso-core:${Versions.AndroidX.espresso}")
}

fun DependencyHandler.defaultDependencies() {
    implementation("androidx.core:core-ktx:${Versions.AndroidX.coreCtx}")
    implementation("androidx.appcompat:appcompat:${Versions.AndroidX.appCompat}")
    implementation("com.google.android.material:material:${Versions.AndroidX.material}")
}

fun DependencyHandler.navigationAnimationDependency() {
    implementation("com.google.accompanist:accompanist-navigation-animation:${Versions.Utils.navigationAnimation}")
}

fun DependencyHandler.splashScreenDependency() {
    implementation("androidx.core:core-splashscreen:${Versions.AndroidX.splashScreen}")
}

fun DependencyHandler.leakCanaryDependency() {
    debugImplementation("com.squareup.leakcanary:leakcanary-android:${Versions.Utils.leakCanary}")
}
