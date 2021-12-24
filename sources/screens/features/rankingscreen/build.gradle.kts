plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-kapt")
}

android {
    compileSdk = 31

    defaultConfig {
        minSdk = 26
        targetSdk = 31
//        versionCode = 1
//        versionName = "1.0"

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

    implementation(project(":core"))
    implementation(project(":utils"))
    implementation(project(":timespan"))

    defaultDependencies()
    implementation("androidx.constraintlayout:constraintlayout:2.1.2")
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