# Cool 3D Minesweeper

### Table of contents
-- [Description](#description)
-- [Technologies](#technologies)
-- [Features](#features)

----
## Description
3D minesweeper game inspired by "Dig Mines" (com.kittoworks.digmines3d).


### Technologies
- dagger2;
- compose;
- openGL ES;
- ROOM.

## Features
### DSL to declare project modules.
See 'projectDir' and 'ParentDirHolder.subfolder' routines in './settings.gradle.kts';
### Finite state machine.
- See 'finitestatemachine' module in './sources/logic/common' folder;
- This functionality is used in 'TemplateViewModel' abstract class;
- Some of the most important functionality of the finite state machine are in 'com/surovtsev/finitestatemachine/FiniteStateMachine' and 'com/surovtsev/finitestatemachine/helpers/EventProcessorHelper' (look at 'processEvent' and 'tryToProcessEvent' routines);
- Finite state machine aggregates restartable coroutine scope ('restartableCoroutineScopeEntryPoint' field in 'com/surovtsev/finitestatemachine/FiniteStateMachine');
## Compose
- navigation (see 'com/surovtsev/cool3dminesweeper/presentation/MainActivity') in app module with animation 'val navAnimHelper = SimpleNavigationAnimationHelper(...)';
- screens:
    - Main: 
