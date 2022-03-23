
/// region [action]: declare project modules tree
projectDir(
    Folders.root,
    listOf(Modules.app),
) {
    subfolder(
        Folders.logic,
    ) {
        subfolder(
           Folders.common,
           listOf(
               Modules.finiteStateMachine,
               Modules.templateViewModel,
               Modules.timeSpan,
               Modules.touchListener,
               Modules.utils,
           ),
        ) {
            subfolder(
                Folders.restartableCoroutineScope,
                listOf(
                    Modules.restartableCoroutineScope,
                    Modules.subscriptionsHolder,
                )
            )
        }

        subfolder(
            Folders.specific,
            listOf(
                Modules.core,
                Modules.gameLogic,
            )
        ) {
            subfolder(
                Folders.gameStateHolder,
                listOf(
                    Modules.gameState,
                    Modules.gameStateHolder,
                )
            )
        }
    }

    subfolder(
        Folders.screens,
        listOf(
            Modules.gameScreen,
            Modules.mainScreen,
        ),
    ) {
        subfolder(
            Folders.optional,
            listOf(
                Modules.helpScreen,
                Modules.rankingScreen,
                Modules.settingsScreen,
            ),
        )
    }
}
/// endregion


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
    const val templateViewModel = ":templateviewmodel"
    const val timeSpan = ":timespan"
    const val touchListener = ":touchlistener"
    const val utils = ":utils"
}


object Folders {
    const val common = "common"
    const val gameStateHolder = "gamestateholder"
    const val logic = "logic"
    const val optional = "optional"
    const val restartableCoroutineScope = "restartablecoroutinescope"
    const val root = "sources"
    const val screens = "screens"
    const val specific = "specific"
}
/// endregion


/// region [helper methods]: dsl to declare project modules tree
fun projectDir(
    dir: String,
    modules: List<String> = emptyList(),
    subDirsCreator: (ParentDirHolder.() -> Unit)? = null,
) {
    includeModules(
        modules,
        dir
    )
    subDirsCreator?.invoke(ParentDirHolder(dir))
}

fun ParentDirHolder.subfolder(
    folderName: String,
    modules: List<String> = emptyList(),
    subDirsCreator: (ParentDirHolder.() -> Unit)? = null,
) {
    projectDir(
        this.subfolderPath(folderName),
        modules,
        subDirsCreator
    )
}

data class ParentDirHolder(
    val parentDir: String,
) {
    fun subfolderPath(
        folderName: String
    ) = "$parentDir/$folderName"
}

fun includeModules(
    modules: List<String>,
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
    // remove colon. e.g. :app -> app
    project(name).projectDir = File(rootDir, "$dir/${name.substring(1)}")
}
/// endregion
