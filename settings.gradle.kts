/*
MIT License

Copyright (c) [2022] [Alexander Surovtsev]

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */


/// region [action]: declare project modules tree

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "cool_3d_minesweeper"

projectDir(
    Folders.root,
    listOf(
        Modules.app
    ),
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
                Modules.videoTutorialScreen,
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
    const val videoTutorialScreen = ":videotutorialscreen"
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
