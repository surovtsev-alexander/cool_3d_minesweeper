object ProjectModules {
    object App {
        const val app = ":app"
    }

    object Logic {
        object Common {
            const val finiteStateMachine = ":finitestatemachine"
            const val timeSpan = ":timespan"
            const val touchListener = ":touchlistener"
            const val utils = ":utils"

            object RestartableCoroutineScope {
                const val restartableCoroutineScope = ":restartablecoroutinescope"
                const val subscriptionsHolder = ":subscriptionsholder"
            }
        }

        object Specific {
            const val core = ":core"
            const val gameLogic = ":gamelogic"
            const val gameState = ":gamestate"
            const val gameStateHolder = ":gamestateholder"
        }
    }

    object Screens {
        const val mainScreen = ":mainscreen"
        const val gameScreen = ":gamescreen"

        object Optional {
            const val rankingScreen = ":rankingscreen"
            const val settingsScreen = ":settingsscreen"
            const val helpScreen = ":helpscreen"
        }
    }

}
