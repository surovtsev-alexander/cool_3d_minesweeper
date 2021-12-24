rootProject.name = "cool_3d_minesweeper"

fun includeModule(
    name: String,
    dir: String
) {
    include(name)
    project(name).projectDir = File(rootDir, "$dir/${name.substring(1)}")
}

fun includeModules(
    modules: Array<String>,
    dir: String
) {
    modules.map {
        includeModule(
            it,
            dir
        )
    }
}


val appModule = ":app"

val appModuleDir = "sources"

includeModule(
    appModule,
    appModuleDir
)


val coreModule = ":core"
val utilsModule = ":utils"
val touchListenerModule = ":touchlistener"
val gameLogicModule = ":gamelogic"
val timeSpanModule = ":timespan"

val coreModules = arrayOf(
    coreModule,
    utilsModule,
    touchListenerModule,
    gameLogicModule,
    timeSpanModule,
)
val logicDir = "sources/logic"

includeModules(
    coreModules,
    logicDir,
)


val mainScreenModule = ":mainscreen"
val gameScreenModule = ":gamescreen"

val screensModules = arrayOf(
    mainScreenModule,
    gameScreenModule,
)
val screensDir = "sources/screens"

includeModules(
    screensModules,
    screensDir,
)

val rankingScreenModule = ":rankingscreen"
val settingsScreenModule = ":settingsscreen"
val helpScreenModule = ":helpscreen"

val featuresScreensModules = arrayOf(
    rankingScreenModule,
    settingsScreenModule,
    helpScreenModule,
)
val featuresScreensDir = "sources/screens/features"

includeModules(
    featuresScreensModules,
    featuresScreensDir,
)
