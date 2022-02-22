plugins {
    id(Plugins.ANDROID_LIBRARY)
    id(Plugins.KOTLIN_ANDROID)
    id(Plugins.KOTLIN_KAPT)
}

android {
    compileSdk = Versions.Sdk.compileSdk

    defaultConfig {
        minSdk = Versions.Sdk.minSdk
        targetSdk = Versions.Sdk.targetSdk

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = Versions.AndroidX.compose
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    includeProjectModules(
        arrayOf(
            ProjectModules.Logic.Common.finiteStateMachine,
            ProjectModules.Logic.Common.utils,
            ProjectModules.Logic.Common.RestartableCoroutineScope.restartableCoroutineScope,
            ProjectModules.Logic.Common.RestartableCoroutineScope.subscriptionsHolder,
            ProjectModules.Logic.Specific.core,
        )
    )

    defaultDependencies()

    testDependencies()

    daggerDependencies()

    coroutinesDependency()


    commonComposeDependencies()

    implementation("androidx.activity:activity-compose:1.4.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.4.0")
    implementation("androidx.compose.ui:ui-tooling:${Versions.AndroidX.compose}")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")

    navigationDependencies()

    glmDependency()

    logcatDependency()

}
