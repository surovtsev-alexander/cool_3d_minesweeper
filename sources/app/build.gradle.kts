
plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
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

    implementation(project(":core"))
    implementation(project(":utils"))
    implementation(project(":mainscreen"))
    implementation(project(":gamescreen"))
    implementation(project(":rankingscreen"))
    implementation(project(":settingsscreen"))
    implementation(project(":helpscreen"))


    implementation("org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}")
    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.appcompat:appcompat:1.4.0")
    implementation("com.google.android.material:material:1.4.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.2")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    implementation("org.jetbrains.anko:anko-common:${Versions.anko}")

    implementation("com.github.kotlin-graphics:glm:v1.0.1")

    implementation("com.google.code.gson:gson:2.8.9")

    implementation("androidx.recyclerview:recyclerview:1.2.1")
    implementation("androidx.cardview:cardview:1.0.0")

    implementation("androidx.compose.ui:ui:${Versions.compose}")
    implementation("androidx.activity:activity-compose:1.4.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.4.0")
    implementation("androidx.compose.material:material:${Versions.compose}")
    implementation("androidx.compose.ui:ui-tooling:${Versions.compose}")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.compose.runtime:runtime-livedata:${Versions.compose}")


    implementation("androidx.fragment:fragment-ktx:${Versions.fragment}")

    // time
    implementation("com.jakewharton.threetenabp:threetenabp:1.3.1")

    // navigation
    // Kotlin
    implementation("androidx.navigation:navigation-fragment-ktx:${Versions.nav}")
    implementation("androidx.navigation:navigation-ui-ktx:${Versions.nav}")

    // Feature module Support
    implementation("androidx.navigation:navigation-dynamic-features-fragment:${Versions.nav}")

    // Testing Navigation
    androidTestImplementation("androidx.navigation:navigation-testing:${Versions.nav}")

    // Jetpack Compose Integration
    implementation("androidx.navigation:navigation-compose:${Versions.composeNavigation}")


    implementation("com.google.accompanist:accompanist-navigation-animation:${Versions.animNavVersion}")

    //end of navigation


    //Dagger - Hilt
//    implementation "com.google.dagger:hilt-android:2.38.1"
//    kapt "com.google.dagger:hilt-android-compiler:2.37"
//    implementation "androidx.hilt:hilt-lifecycle-viewmodel:1.0.0-alpha03"
//    kapt "androidx.hilt:hilt-compiler:1.0.0"
//    implementation "androidx.hilt:hilt-navigation-compose:1.0.0-beta01"

    implementation("com.google.dagger:dagger:${Versions.dagger}")
    kapt("com.google.dagger:dagger-compiler:${Versions.dagger}")

    //region Lifecycle
    implementation("androidx.lifecycle:lifecycle-common-java8:${Versions.lifecycle}")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifecycle}")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycle}")

    // Square logcat
    implementation("com.squareup.logcat:logcat:0.1")

    implementation("androidx.room:room-ktx:${Versions.room}")
    kapt("androidx.room:room-compiler:${Versions.room}")

    implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:2.4.0")

    implementation("androidx.core:core-splashscreen:1.0.0-alpha02")

    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.7")
}
