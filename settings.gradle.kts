rootProject.name = "cool_3d_minesweeper"


/// region [values]: modules descriptions
val appModule = ":app"

val appModuleDir = "sources"


val coreModule = ":core"
val finiteStateMachineModule = ":finitestatemachine"
val gameLogicModule = ":gamelogic"
val gameStateModule = ":gamestate"
val gameStateHolderModule = ":gamestateholder"
val restartableCoroutineScopeModule = ":restartablecoroutinescope"
val subscriptionsHolderModule = ":subscriptionsholder"
val timeSpanModule = ":timespan"
val touchListenerModule = ":touchlistener"
val utilsModule = ":utils"

val coreModules = arrayOf(
    coreModule,
    finiteStateMachineModule,
    gameLogicModule,
    gameStateModule,
    gameStateHolderModule,
    restartableCoroutineScopeModule,
    subscriptionsHolderModule,
    timeSpanModule,
    touchListenerModule,
    utilsModule,
)
val logicDir = "sources/logic"


val gameScreenModule = ":gamescreen"
val mainScreenModule = ":mainscreen"

val screensModules = arrayOf(
    gameScreenModule,
    mainScreenModule,
)
val screensDir = "sources/screens"


val helpScreenModule = ":helpscreen"
val rankingScreenModule = ":rankingscreen"
val settingsScreenModule = ":settingsscreen"

val featuresScreensModules = arrayOf(
    helpScreenModule,
    rankingScreenModule,
    settingsScreenModule,
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
