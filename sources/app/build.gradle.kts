
plugins {
    id(Plugins.ANDROID_APPLICATION)
    id(Plugins.KOTLIN_ANDROID)
    id(Plugins.KOTLIN_KAPT)
}

android {
    compileSdk = Versions.compileSdk

    defaultConfig {
        applicationId = "com.surovtsev.cool_3d_minesweeper"
        minSdk = Versions.minSdk
        targetSdk = Versions.targetSdk
        versionCode = 1
        versionName = "1.0"

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
        kotlinCompilerExtensionVersion = Versions.compose
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
            ProjectModules.Core.core,
            ProjectModules.Core.utils,
            ProjectModules.Screens.mainScreen,
            ProjectModules.Screens.gameScreen,
            ProjectModules.FeatureScreens.rankingScreen,
            ProjectModules.FeatureScreens.settingsScreen,
            ProjectModules.FeatureScreens.helpScreen,
        )
    )

    implementation("org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}")

    defaultDependencies()

    implementation("androidx.constraintlayout:constraintlayout:2.1.2")

    testDependencies()

    glmDependency()

    gsonDependency()

    implementation("androidx.recyclerview:recyclerview:1.2.1")
    implementation("androidx.cardview:cardview:1.0.0")

    commonComposeDependencies()

    implementation("androidx.activity:activity-compose:1.4.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.4.0")
    implementation("androidx.compose.ui:ui-tooling:${Versions.compose}")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")


    implementation("androidx.fragment:fragment-ktx:${Versions.fragment}")

    threetenabpDependency()

    kotlinNavigationDependencies()

    // Feature module Support
    implementation("androidx.navigation:navigation-dynamic-features-fragment:${Versions.nav}")

    // Testing Navigation
    androidTestImplementation("androidx.navigation:navigation-testing:${Versions.nav}")

    composeNavigationDependency()


    implementation("com.google.accompanist:accompanist-navigation-animation:${Versions.animNavVersion}")

    //end of navigation

    daggerDependencies()

    //region Lifecycle
    implementation("androidx.lifecycle:lifecycle-common-java8:${Versions.lifecycle}")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifecycle}")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycle}")

    logcatDependency()

    roomDependencies()


    implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:2.4.0")

    implementation("androidx.core:core-splashscreen:1.0.0-alpha02")

    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.7")
}
