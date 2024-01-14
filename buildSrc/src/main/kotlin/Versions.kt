
object Versions {
    object Application {
        const val id = "com.surovtsev.cool_3d_minesweeper"
    }

    object Sdk {
        const val compileSdk = 34
        const val minSdk = 28
    }

    object Library {
        private const val major = 1 // change when you make incompatible API changes
        private const val minor = 5 // change when you add functionality in a backward-compatible manner
        private const val build = 1 // change when you make backward-compatible bug fixes

        const val versionCode = (major * 100 + minor) * 100 + build
        const val versionName = "$major.$minor.$build"
    }

    const val dagger = "2.50"
    const val gradle = "8.2.0"

    object Google {
        const val gson = "2.8.9"
    }

    object Kotlin {
        const val kotlin = "1.9.22"
        const val coroutines = "1.7.3"
    }


    object JUnit {
        const val junit = "4.13.2"
        const val junitExt = "1.1.3"
    }

    object AndroidX {
        const val appCompat = "1.6.1"
        const val compose = "1.5.4"
        const val composeNavigation = "2.7.6"
        const val coreCtx = "1.12.0"
        const val espresso = "3.4.0"
        const val material = "1.11.0"
        const val navigation = "2.7.6"
        const val room = "2.6.1"
        const val splashScreen = "1.0.0-beta01"
    }

    object Utils {
        const val glm = "0.9.9.1-12"
        const val leakCanary = "2.7"
        const val logcat = "0.1"
        const val navigationAnimation = "0.32.0"
        const val threetenabp = "1.3.1"
        const val exoPlayer = "2.19.1"
    }
}
