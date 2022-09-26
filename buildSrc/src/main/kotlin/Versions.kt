
object Versions {
    object Application {
        const val id = "com.surovtsev.cool_3d_minesweeper"
    }

    object Sdk {
        const val compileSdk = 31
        const val targetSdk = 31
        const val minSdk = 26
    }

    object Library {
        private const val major = 1 // change when you make incompatible API changes
        private const val minor = 5 // change when you add functionality in a backward-compatible manner
        private const val build = 1 // change when you make backward-compatible bug fixes

        const val versionCode = (major * 100 + minor) * 100 + build
        const val versionName = "$major.$minor.$build"
    }

    const val dagger = "2.40.3"
    const val gradle = "7.0.4"

    object Google {
        const val gson = "2.8.9"
    }

    object Kotlin {
        const val kotlin = "1.6.0"
        const val coroutines = "1.6.0-RC2"
    }


    object JUnit {
        const val junit = "4.13.2"
        const val junitExt = "1.1.3"
    }

    object AndroidX {
        const val appCompat = "1.4.1"
        const val compose = "1.1.0-rc01"
        const val composeNavigation = "2.4.0-rc01"
        const val coreCtx = "1.7.0"
        const val espresso = "3.4.0"
        const val material = "1.5.0"
        const val navigation = "2.3.5"
        const val room = "2.4.0"
        const val splashScreen = "1.0.0-beta01"
    }

    object Utils {
        const val glm = "v1.0.1"
        const val leakCanary = "2.7"
        const val logcat = "0.1"
        const val navigationAnimation = "0.21.4-beta"
        const val threetenabp = "1.3.1"
        const val exoPlayer = "2.17.1"
    }
}
