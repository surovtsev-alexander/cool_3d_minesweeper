// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        topLevelDependencies()
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        maven {
            url = java.net.URI("https://jitpack.io")
        }
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
