plugins {
    id(Plugins.ANDROID_LIBRARY)
    id(Plugins.KOTLIN_ANDROID)
    id(Plugins.KOTLIN_KAPT)
}

android {
    namespace = "com.surovtsev.timespan"

    compileSdk = Versions.Sdk.compileSdk

    defaultConfig {
        minSdk = Versions.Sdk.minSdk

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        listOf(
            ProjectModules.Logic.Common.utils,
            ProjectModules.Logic.Specific.core,
        )
    )

    defaultDependencies()
    testDependencies()

    daggerDependencies()

    coroutinesDependency()

}