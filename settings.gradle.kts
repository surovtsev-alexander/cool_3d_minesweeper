rootProject.name = "cool_3d_minesweeper"


/// region [values]: constants (Modules and Folders)
object Modules {
    const val app = ":app"
    const val core = ":core"
    const val finiteStateMachine = ":finitestatemachine"
    const val helpScreen = ":helpscreen"
    const val gameLogic = ":gamelogic"
    const val gameScreen = ":gamescreen"
    const val gameState = ":gamestate"
    const val gameStateHolder = ":gamestateholder"
    const val mainScreen = ":mainscreen"
    const val rankingScreen = ":rankingscreen"
    const val restartableCoroutineScope = ":restartablecoroutinescope"
    const val settingsScreen = ":settingsscreen"
    const val subscriptionsHolder = ":subscriptionsholder"
    const val timeSpan = ":timespan"
    const val touchListener = ":touchlistener"
    const val utils = ":utils"
}


object Folders {
    const val common = "common"
    const val logic = "logic"
    const val optional = "optional"
    const val restartableCoroutineScope = "restartablecoroutinescope"
    const val root = "sources"
    const val screens = "screens"
    const val specific = "specific"
}
/// endregion


/// region [action]: declare project modules tree
projectDir(
    Folders.root,
    arrayOf(Modules.app),
) {
    projectDir(
        subfolder(Folders.logic),
    ) {
        projectDir(
           subfolder(Folders.common),
           arrayOf(
               Modules.finiteStateMachine,
               Modules.timeSpan,
               Modules.touchListener,
               Modules.utils,
           ),
        ) {
            projectDir(
                subfolder(Folders.restartableCoroutineScope),
                arrayOf(
                    Modules.restartableCoroutineScope,
                    Modules.subscriptionsHolder,
                )
            )
        }

        projectDir(
            subfolder(Folders.specific),
            arrayOf(
                Modules.core,
                Modules.gameLogic,
                Modules.gameState,
                Modules.gameStateHolder,
            )
        )
    }

    projectDir(
        subfolder(Folders.screens),
        arrayOf(
            Modules.gameScreen,
            Modules.mainScreen,
        ),
    ) {
        projectDir(
            subfolder(Folders.optional),
            arrayOf(
                Modules.helpScreen,
                Modules.rankingScreen,
                Modules.settingsScreen,
            ),
        )
    }
}
/// endregion


/// region [helper methods]: include modules of project tree
fun projectDir(
    dir: String,
    modules: Array<String> = emptyArray(),
    subDirsCreator: (ParentDirHolder.() -> Unit)? = null,
) {
    includeModules(
        modules,
        dir
    )
    subDirsCreator?.invoke(ParentDirHolder(dir))
}

data class ParentDirHolder(
    val parentDir: String,
) {
    fun subfolder(
        dir: String
    ) = "$parentDir/$dir"
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

fun includeModule(
    name: String,
    dir: String
) {
    include(name)
    project(name).projectDir = File(rootDir, "$dir/${name.substring(1)}")
}
/// endregion
