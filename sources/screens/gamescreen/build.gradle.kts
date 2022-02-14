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

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = Versions.AndroidX.compose
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
            ProjectModules.Logic.Common.touchListener,
            ProjectModules.Logic.Common.utils,
            ProjectModules.Logic.Specific.core,
            ProjectModules.Logic.Specific.gameLogic,
        )
    )

    defaultDependencies()

    testDependencies()

    daggerDependencies()

    glmDependency()

    coroutinesDependency()

    logcatDependency()

    commonComposeDependencies()

    navigationDependencies()
}
