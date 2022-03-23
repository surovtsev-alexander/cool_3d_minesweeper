plugins {
    id(Plugins.ANDROID_APPLICATION)
    id(Plugins.KOTLIN_ANDROID)
    id(Plugins.KOTLIN_KAPT)
}

android {
    compileSdk = Versions.Sdk.compileSdk

    defaultConfig {
        applicationId = Versions.Application.id

        minSdk = Versions.Sdk.minSdk
        targetSdk = Versions.Sdk.targetSdk
        versionCode = Versions.Library.versionCode
        versionName = Versions.Library.versionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
            ProjectModules.Logic.Common.templateViewModel,
            ProjectModules.Logic.Common.utils,
            ProjectModules.Logic.Specific.core,
            ProjectModules.Screens.mainScreen,
            ProjectModules.Screens.gameScreen,
            ProjectModules.Screens.Optional.rankingScreen,
            ProjectModules.Screens.Optional.settingsScreen,
            ProjectModules.Screens.Optional.helpScreen,
        )
    )

    defaultDependencies()

    testDependencies()

    glmDependency()

    gsonDependency()

    commonComposeDependencies()

    threetenabpDependency()

    navigationDependencies()

    composeNavigationDependency()

    navigationAnimationDependency()

    daggerDependencies()

    logcatDependency()

    roomDependencies()

    splashScreenDependency()

    leakCanaryDependency()
}
