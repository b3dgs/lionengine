## Presentation

The __LionEngine__ is a game engine especially developed during the project [Lionheart Remake] (http://www.b3dgs.com/v6/page.php?lang=en&section=lionheart_remake) for an easy Java use. The engine is as a library, in Jar format (including its javadoc), which can be included in any project; for utility class uses, or to directly implement and inherit a game skeleton (_including management of frame rate, extrapolation, input output..._).

Using Java 7 internal libraries, it is specifically designed for 2D games (no support for 3D at the moment), and proposes a set of functions for 2D resource management (_images, sprites, animations, tiles..._). Inputs and outputs are also available, with an easy keys retrieval, mouse movement... Management of music file are also available (Wav, Midi, and more using plug-ins, such as Sc68 and Ogg). Windowed, full-screen and applet formats are fully supported, with a complete frame rate control.

In its current version, the engine greatly simplifies the development of __Platform__, __Strategy__ and __Shoot'em Up__ games.


## Installation

Steps to include the LionEngine in your project:

* Install at least the [Java JDK 7] (http://www.oracle.com/technetwork/java/javase/downloads/index.html)
* Choose your favourite IDE ([Eclipse] (http://www.eclipse.org/downloads/), [Netbeans] (https://netbeans.org/downloads/)...)
* Download the latest [LionEngine] (http://www.b3dgs.com/v6/page.php?lang=en&section=lionengine)
* Include all LionEngine libraries you need for your project, following the tree dependency:
  * __lionengine-core__ _(minimum requirement)_
  * __lionengine-game__ _(first base for any game development)_
        * __lionengine-game-platform__ _(specialized for platform games)_
        * __lionengine-pathfinding__ _(for pathfinding support)_
            * __lionengine-game-rts__ _(specialized for strategy games)_
        * __lionengine-game-shmup__ _(specialized for shoot'em up games)_
        * __lionengine-network__ _(support for networked games)_
  * __lionengine-midi__ _(allows to play Midi musics)_
  * __lionengine-wav__ _(allows to play Wav sounds)_
  * __lionengine-ogg__ _(allows to play Ogg compressed musics)_
  * __lionengine-sc68__ _(allows to play Sonic Arranger Amiga and Sc68 Atari musics)_
  * __lionengine-tools__ _(special tools around tiles and level conversion)_
* Join (if you want) the javadoc for each library
* You are now ready to use the LionEngine in your project


## Getting Started

Once you installed the LionEngine in your project, you may would like to know how to prepare a quick sample as a first try.

#### Main class
```java
/**
 * Program starts here. When you start the jvm, ensure that this main function is called.
 */
public final class AppFirstCode
{
    /**
     * Private constructor.
     */
    private AppFirstCode()
    {
        throw new RuntimeException();
    }

    /**
     * Main function called by the jvm.
     * 
     * @param args The arguments.
     */
    public static void main(String args[])
    {
        // Start engine (name = "First Code", version = "1.0.0", resources directory = "resources")
        // The Engine is initialized with our parameters:
        // - The name of our program: "First Code"
        // - Our program version: "1.0.0"
        // - The main resources directory, relative to the execution directory: ./resources/
        // This mean that any resources loaded with Media.get(...) will have this directory as prefix
        // - The verbose level
        // - The swing theme (general java appearance)
        Engine.start("First Code", Version.create(1, 0, 0), "resources", Verbose.CRITICAL, Theme.SYSTEM);

        // Configuration reference (native size = 320*240*16 at 60fps)
        // This mean that our native resolution is in 320*240
        // These data are used in case of rendering scaling, if the desired output is different
        // The last value is used to perform the frame rate calculation, corresponding to the native frame rate
        final Display internal = new Display(320, 240, 16, 60);

        // Display configuration (desired = 640*480*16 at 60fps)
        // This is corresponding to the output configuration
        // As our native is in 320*240, the output will be scaled by 2
        // If the current frame rate is lower, the extrapolation value will allow to compensate any data calculation
        final Display external = new Display(640, 480, 16, 60);

        // Final configuration (rendering will be scaled by 2 considering native and desired config)
        // This is the final configuration container, including window mode
        final Config config = new Config(internal, external, true);

        // Program starter, the main thread, setup with our configuration
        // It just needs one sequence reference to start
        final Loader loader = new Loader(config);
        loader.start(new Scene(loader));
    }
}
```

#### Minimal sequence
```java
/**
 * This is where the game loop is running. Any sequence represents a thread handled by the Loader. To link a sequence
 * with another one, a simple call to this.end(sequence) is necessary. This will terminate the current sequence, and
 * start the linked one.
 */
final class Scene
        extends Sequence
{
    /** Text drawer. */
    private final Text text;

    /**
     * Create the scene and its vars.
     * 
     * @param loader The loader reference.
     */
    Scene(final Loader loader)
    {
        super(loader);
        text = new Text(Font.SANS_SERIF, 12, Text.NORMAL);
    }

    @Override
    protected void load()
    {
        text.setText("Hello");
        text.setLocation(width / 2, height / 2 - 8);
        text.setAlign(Align.CENTER);
    }

    @Override
    protected void update(double extrp)
    {
        // Update routine
    }

    @Override
    protected void render(Graphic g)
    {
        // Simple rendering
        text.render(g);
        // Direct rendering
        text.draw(g, width / 2, height / 2 + 8, Align.CENTER, "World");
    }

    /*
     * It is not necessary to override this method
     */
    @Override
    protected void onTerminate()
    {
        UtilityMessageBox.information("Terminate !", "Closing app...");
    }
}
```
