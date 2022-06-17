# Cool 3D Minesweeper

### Table of contents
-- [Description](#description)
-- [Technologies](#technologies)
-- [Features](#features)

----

## Description
3D minesweeper game is inspired by "Dig Mines" (com.kittoworks.digmines3d).

----

## Technologies
- dagger2;
- compose;
- openGL ES;
- ROOM.

----

## Features

### DSL to declare project modules
See `projectDir` and `ParentDirHolder.subfolder` routines in './settings.gradle.kts';

### Finite state machine
- See 'finitestatemachine' module in './sources/logic/common' folder;
- This functionality is used in `TemplateViewModel` abstract class;
- Some of the most important functionality of the finite state machine are in 'com/surovtsev/finitestatemachine/`FiniteStateMachine`' and 'com/surovtsev/finitestatemachine/helpers/`EventProcessorHelper`' (look at `processEvent` and `tryToProcessEvent` routines);
- Finite state machine aggregates restartable coroutine scope (`restartableCoroutineScopeEntryPoint` field in 'com/surovtsev/finitestatemachine/`FiniteStateMachine`');

### RestartableCoroutineScope
Look at `restart` method in `SubscriberImp` (com.surovtsev.utils.coroutines.restartablescope.subscribing.subscriber).

### AABBTree
See `Tree` class in com.surovtsev.gamestate.logic.models.game.aabb.tree package.

### Custom TouchListener
See 'touchlistener' module in 'sources/logic/common/touchlistener' folder.

### OpenGL ES
- Shaders are located at 'sources/logic/specific/gamelogic/src/main/res/raw' folder;
- Look at `MinesweeperOpenGLEventsHandler` class in 'com.surovtsev.gamelogic.minesweeper.interaction.opengleventshandler' package;
- Look at `onDrawFrame`, `onSurfaceCreated`, and `onSurfaceChanged` methods in `GameViewsHolder` class of 'com.surovtsev.gamelogic.models.gles.gameviewsholder' pacakge;
- See `tick` method in `EventHandler` class ('com.surovtsev.gamelogic.minesweeper.interaction.eventhandler').

### Compose
- navigation (see 'com/surovtsev/cool3dminesweeper/presentation/`MainActivity`') in app module with animation `val navAnimHelper = SimpleNavigationAnimationHelper(...)`;
- screens:
    - Main;
    - Game;
    - Ranking;
    - Settings;
    - Video tutorial;
- `ErrorDialogPlacer`:
    - Look at `GameScreenControls` method in 'GameScreen.kt' ('com.surovtsev.gamescreen.presentation');
    - See `ErrorDialog` method in 'ErrorDialog.kt' ('com.surovtsev.templateviewmodel.helpers.errordialog').
