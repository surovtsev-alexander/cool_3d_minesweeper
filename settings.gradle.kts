rootProject.name = "cool_3d_minesweeper"


/// region [values]: modules descriptions
val appModule = ":app"

val appModuleDir = "sources"


val coreModule = ":core"
val utilsModule = ":utils"
val touchListenerModule = ":touchlistener"
val gameLogicModule = ":gamelogic"
val timeSpanModule = ":timespan"
val restartableCoroutineScopeModule = ":restartablecoroutinescope"

val coreModules = arrayOf(
    coreModule,
    utilsModule,
    touchListenerModule,
    gameLogicModule,
    timeSpanModule,
    restartableCoroutineScopeModule,
)
val logicDir = "sources/logic"


val mainScreenModule = ":mainscreen"
val gameScreenModule = ":gamescreen"

val screensModules = arrayOf(
    mainScreenModule,
    gameScreenModule,
)
val screensDir = "sources/screens"


val rankingScreenModule = ":rankingscreen"
val settingsScreenModule = ":settingsscreen"
val helpScreenModule = ":helpscreen"

val featuresScreensModules = arrayOf(
    rankingScreenModule,
    settingsScreenModule,
    helpScreenModule,
)
val featuresScreensDir = "sources/screens/optional"
/// endregion


/// region [action]: including modules
includeModule(
    appModule,
    appModuleDir
)
includeModules(
    coreModules,
    logicDir,
)
includeModules(
    screensModules,
    screensDir,
)
includeModules(
    featuresScreensModules,
    featuresScreensDir,
)
/// endregion


/// region [helper methods]: include modules
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

fun includeModule(
    name: String,
    dir: String
) {
    include(name)
    project(name).projectDir = File(rootDir, "$dir/${name.substring(1)}")
}
/// endregion
