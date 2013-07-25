Presentation

The LionEngine is a game engine especially developed during the project Lionheart Remake for an easy Java use. The engine is as a library, in Jar format (including its javadoc), which can be included in any project; for utility class uses, or to directly implement and inherit a game skeleton (including management of frame rate, extrapolation, input output...).

Using Java 7 internal libraries, it is specifically designed for 2D games (no support for 3D at the moment), and proposes a set of functions for 2D resource management (images, sprites, animations, tiles...). Inputs and outputs are also available, with an easy keys retrieval, mouse movement... Management of music file are also available (Wav, Midi, and more using plug-ins, such as Sc68 and Ogg). Windowed, full-screen and applet formats are fully supported, with a complete frame rate control.

In its current version, the engine greatly simplifies the development of Platform, Strategy and Shoot'em Up games.


Installation

Steps to include the LionEngine in your project:

* Install at least the Java JDK 7
* Choose your favourite IDE (Eclipse, Netbeans...)
* Download the latest LionEngine
* Include all LionEngine libraries you need for your project, following the tree dependency:
** lionengine-core (minimum requirement)
** lionengine-game (first base for any game development)
*** lionengine-game-platform (specialized for platform games)
*** lionengine-pathfinding (for pathfinding support)
**** lionengine-game-rts (specialized for strategy games)
*** lionengine-game-shmup (specialized for shoot'em up games)
** lionengine-network (support for networked games)
** lionengine-midi (allows to play Midi musics)
** lionengine-wav (allows to play Wav sounds)
** lionengine-ogg (allows to play Ogg compressed musics)
** lionengine-sc68 (allows to play Sonic Arranger Amiga and Sc68 Atari musics)
** lionengine-tools (special tools around tiles and level conversion)
* Join (if you want) the javadoc for each library
* You are now ready to use the LionEngine in your project

