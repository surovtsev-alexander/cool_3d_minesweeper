plugins {
    id(Plugins.ANDROID_LIBRARY)
    id(Plugins.KOTLIN_ANDROID)
    id(Plugins.KOTLIN_KAPT)
}

android {
    compileSdk = Versions.compileSdk

    defaultConfig {
        minSdk = Versions.minSdk
        targetSdk = Versions.targetSdk

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = Versions.compose
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
            ProjectModules.Logic.Common.RestartableCoroutineScope.restartableCoroutineScope,
            ProjectModules.Logic.Common.RestartableCoroutineScope.subscriptionsHolder,
            ProjectModules.Logic.Common.timeSpan,
            ProjectModules.Logic.Common.utils,
            ProjectModules.Logic.Specific.core,
        )
    )

    defaultDependencies()
    implementation("androidx.constraintlayout:constraintlayout:2.1.3")
    testDependencies()


    daggerDependencies()

    coroutinesDependency()


    commonComposeDependencies()

    implementation("androidx.activity:activity-compose:1.4.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.4.0")
    implementation("androidx.compose.ui:ui-tooling:${Versions.compose}")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")


    kotlinNavigationDependencies()


    glmDependency()

    threetenabpDependency()


    logcatDependency()

}