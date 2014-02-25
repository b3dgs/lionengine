<div align="center"><a href="http://www.b3dgs.com/v6/page.php?lang=en&section=lionengine"><img align=center src="http://www.b3dgs.com/v6/projects/lionengine/lionengine.jpg"/></a></div>

<div align="right">
 <table><tr><td>
  <h4 align="center">Summary</h4>
  <div align="left">
   <ul>
    <li><a href="#presentation">Presentation</a></li>
    <li><a href="#general-features">General features</a></li>
    <li><a href="#download">Download</a></li>
    <li><a href="#installation">Installation</a></li>
    <li><a href="#getting-started">Getting Started</a></li>
    <li><a href="#tutorials">Tutorials</a></li>
   </ul>
  </div>
 </td></tr></table>
</div>

## Presentation

The __LionEngine__ is a game engine especially developed during the project [Lionheart Remake] (http://www.b3dgs.com/v6/page.php?lang=en&section=lionheart_remake) for an easy Java use.
The engine is as a library, in Jar format (_including its javadoc_), which can be included in any project;
for utility class uses, or to directly implement and inherit a game skeleton (_including management of frame rate, extrapolation, input output..._).

Using Java 7 internal libraries, it is specifically designed for 2D games (no support for 3D at the moment), and proposes a set of functions for 2D resources management (_images_, _sprites_, _animations_, _tiles_...).
Inputs and outputs are also available, with an easy keys retrieval, mouse movement... Management of music file are also available (_Wav_, _Midi_, and more using plug-ins, such as _Sc68_).
Windowed, full-screen and applet formats are fully supported, with a complete frame rate control.

In its current version, the engine greatly simplifies the development of __Platform__, __Strategy__ and __Shoot'em Up__ games, and also __Network__ layer.

Since the version __6.0.0__, it supports __Android 2.3.3__ *(API10)*.
The only change to perform is the gameplay part, as the '__mouse__' and '__keyboard__' concepts are different on Android.
Everything else is fully compatible and does not require any changes.

## General Features

* #### __lionengine-core__
>  * Simple initialization, with version control, screen configuration and resource directory
>  * Extrapolation control (_machine speed independent_)
>  * Advanced filtering capability (_Bilinear, HQ2X, HQ3X_)
>  * Sequence control (_intro, menu, game part, credits..._)
>  * Easy resource management (_relative to resource directory, without caring of path separator_)
>  * Advanced image usage (_sprite, animated sprite, tile based sprite, image based font, parallax_)
>  * File I/O (_binary & XML reader & writer_)
>  * Utility classes (_Random, Conversions, Maths, File..._)
>  * Verbosity control

* #### __lionengine-java2d__
>  * Engine implementation using __java.awt__ (_for graphics_) and __javax__ (_for Midi & Wav_)
>  * Swing utility (_useful for quick level editor development_)

* #### __lionengine-android__
>  * Engine implementation using __Android 2.3.3 (API10)__


* #### __lionengine-game__
>  * Tile based map package (_with minimap support, native save & load function_)
>  * Projectile system (_with a duo: launcher / projectile_)
>  * Effect system (_and its handler_)
>  * Entity base (_support external XML configuration, gravity and collision_)
>  * Camera management (_view and movement_)
>  * Cursor (_synced or not to system pointer_)
>  * General object factory system (_create instance of entity / effect / projectile from a simple enum_)
>  * Tile extractor (_generate tilesheet from a level rip image_)
>  * Level rip converter (_generate a level data file from a tile sheet and a level rip image_)
  

* #### __lionengine-game-platform__
>  * Background package (_for an easy background composition_)
>  * Extended entity & map package (_including compatibility with raster bar effect_)
>    * Ray cast collision system
>  * Extended camera system
  

* #### __lionengine-game-pathfinding__
>  * __A Star (A*)__ pathfinding implementation
>  * Compatible with game map system


* #### __lionengine-game-rts__
>  * Extended map package (_adding support to pathfinding and fog of war_)
>  * Extended entity package (_supporting separated ability:_ **_attacker_**, **_extractor_**, **_mover_**, **_producer_** _..._)
>  * Entity skill system (_representing its actions, accessible from icons_)
>  * Control panel system (_map area for game action, HUD area for icons and more_)
>  * Extended cursor (_can interact with entities on map and control panel_)


* #### __lionengine-network__
>  * Server & Client system
>  * Customizable network message
>  * Integrated chat system


* #### __lionengine-sc68__
>  * Support for Sc68 Atari music

## Download

* [Go to website](http://www.b3dgs.com/v6/page.php?lang=en&section=lionengine)
* [Last version](http://www.b3dgs.com/v6/projects/lionengine/files/LionEngine_6.0.0_lib.zip)

## Installation

Steps to include the __LionEngine__ in your project:

1. Install at least the [Java JDK 7] (http://www.oracle.com/technetwork/java/javase/downloads/index.html)
2. Install the [Android SDK 2.3.3] (http://developer.android.com/sdk/index.html) (only if you use __lionengine-android__)
3. Choose your favourite IDE ([Eclipse] (http://www.eclipse.org/downloads/), [Netbeans] (https://netbeans.org/downloads/)...)
4. Download the latest [LionEngine] (http://www.b3dgs.com/v6/page.php?lang=en&section=lionengine)
5. Include all __LionEngine__ libraries you need for your project, following the tree dependency:
  * __lionengine-core__ _(minimum requirement)_
    * __lionengine-java2d__ _(required if you use the full_ **_JDK7_**, _target for computer)_
    * __lionengine-android__ _(required if you use_ **_Android 2.3.3_**, _target for phones)_
    * __lionengine-game__ _(base for game development)_
    * __lionengine-game-platform__ _(specialized for platform games)_
      * __lionengine-game-pathfinding__ _(support for pathfinding)_
        * __lionengine-game-rts__ _(specialized for strategy games)_
    * __lionengine-network__ _(support for network)_
    * __lionengine-sc68__ _(support for Sc68 Atari musics)_
6. Join (if you want) the javadoc for each library
7. You are now ready to use the __LionEngine__ in your project

## Getting Started

Once you installed the __LionEngine__ in your project, you may would like to know how to prepare a quick sample as a first try.

#### Main class

* Using **_lionengine-java2d_**
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

* Using **_lionengine-android_**
  ```java
  public final class AppMinimal
          extends Activity
  {
      @Override
      protected void onPostCreate(Bundle savedInstanceState)
      {
          super.onPostCreate(savedInstanceState);

          Engine.start("Minimal", Version.create(1, 0, 0), this);
          final Resolution output = new Resolution(800, 480, 60);
          final Config config = new Config(output, 32, false);
          final Loader loader = new Loader(config);
          loader.start(new Scene(loader));
      }

      @Override
      public void finish()
      {
          super.finish();
          Engine.terminate();
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
        // Update
    }

    @Override
    protected void render(Graphic g)
    {
        // Render
    }
}
```

## Tutorials

* [Go to tutorial index](../../wiki)
