/*
MIT License

Copyright (c) [2022] [Alexander Surovtsev]

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */


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
        listOf(
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
