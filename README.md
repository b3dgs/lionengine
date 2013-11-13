## Presentation

The __LionEngine__ is a game engine especially developed during the project [Lionheart Remake] (http://www.b3dgs.com/v6/page.php?lang=en&section=lionheart_remake) for an easy Java use. The engine is as a library, in Jar format (including its javadoc), which can be included in any project; for utility class uses, or to directly implement and inherit a game skeleton ( _including management of frame rate, extrapolation, input output..._).

Using Java 7 internal libraries, it is specifically designed for 2D games (no support for 3D at the moment), and proposes a set of functions for 2D resource management ( _images_, _sprites_, _animations_, _tiles_...). Inputs and outputs are also available, with an easy keys retrieval, mouse movement... Management of music file are also available ( _Wav_, _Midi_, and more using plug-ins, such as _Sc68_ and _Ogg_). Windowed, full-screen and applet formats are fully supported, with a complete frame rate control.

In its current version, the engine greatly simplifies the development of __Platform__, __Strategy__ and __Shoot'em Up__ games, and also __networked__ game.


## Installation

Steps to include the LionEngine in your project:

* Install at least the [Java JDK 7] (http://www.oracle.com/technetwork/java/javase/downloads/index.html)
* Choose your favourite IDE ([Eclipse] (http://www.eclipse.org/downloads/), [Netbeans] (https://netbeans.org/downloads/)...)
* Download the latest [LionEngine] (http://www.b3dgs.com/v6/page.php?lang=en&section=lionengine)
* Include all LionEngine libraries you need for your project, following the tree dependency:
  * __lionengine-core__ _(minimum requirement)_
        * __lionengine-java2d__ _(required if you use the full JDK7)_
        * __lionengine-android__ _(required if you use Android 2.3.3)_
        * __lionengine-game__ _(first base for any game development)_
          * __lionengine-game-platform__ _(specialized for platform games)_
          * __lionengine-game-pathfinding__ _(for pathfinding support)_
             * __lionengine-game-rts__ _(specialized for strategy games)_
        * __lionengine-network__ _(support for network)_
        * __lionengine-midi__ _(allows to play Midi musics)_
        * __lionengine-wav__ _(allows to play Wav sounds)_
        * __lionengine-ogg__ _(allows to play Ogg compressed musics)_
        * __lionengine-sc68__ _(allows to play Sonic Arranger Amiga and Sc68 Atari musics)_
* Join (if you want) the javadoc for each library
* You are now ready to use the LionEngine in your project


## Getting Started

Once you installed the LionEngine in your project, you may would like to know how to prepare a quick sample as a first try.

#### Main class
```java
public final class AppMinimal
{
    public static void main(String[] args)
    {
        Engine.start("Minimal", Version.create(1, 0, 0), "resources");

        final Resolution output = new Resolution(640, 480, 60);
        final Config config = new Config(output, 16, true);
        final Loader loader = new Loader(config);
        loader.start(new Scene(loader));
    }
}
```

#### Minimal sequence
```java
final class Scene
        extends Sequence
{
    private static final Resolution NATIVE = new Resolution(320, 240, 60);

    Scene(Loader loader)
    {
        super(loader, Scene.NATIVE);
    }

    @Override
    protected void load()
    {
        // Load
    }

    @Override
    protected void update(double extrp)
    {
        if (keyboard.isPressed(Key.ESCAPE))
        {
            end();
        }
        // Update
    }

    @Override
    protected void render(Graphic g)
    {
        // Render
    }
}
```
