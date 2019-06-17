|[![Build Status](https://travis-ci.org/b3dgs/lionengine.svg?branch=master)](https://travis-ci.org/b3dgs/lionengine)<br>[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=com.b3dgs.lionengine%3Alionengine-parent&metric=coverage)](https://sonarcloud.io/dashboard?id=com.b3dgs.lionengine%3Alionengine-parent)<br>[![Lines of code](https://sonarcloud.io/api/project_badges/measure?project=com.b3dgs.lionengine%3Alionengine-parent&metric=ncloc)](https://sonarcloud.io/dashboard?id=com.b3dgs.lionengine%3Alionengine-parent)<br>[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.b3dgs.lionengine/lionengine-core/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.b3dgs.lionengine/lionengine-core)<br>[![License: GPL v3](https://img.shields.io/badge/License-GPL%20v3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0) | <a href="https://www.b3dgs.com/v7/page.php?lang=en&section=lionengine"><img hspace="170" src="https://user-images.githubusercontent.com/34600369/41530953-b6f4554a-72e9-11e8-9ab1-e49d390a9117.png" width="200"/></a> | [Presentation](#presentation)<br>[General features](#general-features)<br>[Download](#download)<br>[Installation](#installation)<br>[Getting Started](#getting-started)<br>[Tutorials](#tutorials) |
|:---|:---:|---:|

## Presentation

The __LionEngine__ is a game engine especially developed during the project [Lionheart Remake](http://www.b3dgs.com/v7/page.php?lang=en&section=lionheart_remake) for an easy Java use.
The engine is as a library, in Jar format (_including its javadoc_), which can be included in any project;
for utility class uses, or to directly implement and inherit a game skeleton (_including management of frame rate, extrapolation, input output..._).

Using Java 8 internal libraries, it is specifically designed for 2D games (no support for 3D at the moment), and proposes a set of functions for 2D resources management (_images_, _sprites_, _animations_, _tiles_...).
Inputs and outputs are also available, with an easy keys retrieval, mouse movement... Management of music file are also available (_Wav_, _Midi_, and more using plug-ins, such as _Sc68_, _AdPlug_ and _AdlMidi_).
Windowed, full-screen and applet formats are fully supported, with a complete frame rate control.

In its current version, the engine greatly simplifies the development of __Platform__, __Strategy__ and __Shoot'em Up__ games, and also __Network__ layer.

[![Overview](http://lionengine.b3dgs.com/v9-0/img/home/overview_en.png)](http://lionengine.b3dgs.com/v9-0/page.php?lang=en&section=home)

## General Features

* #### __lionengine-core__
>  * Simple initialization, with version control, screen configuration and resource directory
>  * Extrapolation control (_machine speed independent_)
>  * Advanced filtering capability (_Bilinear, HQ2X, HQ3X_)
>  * Sequence control (_intro, menu, game part, credits..._)
>  * Easy resource management (_relative to resource directory_)
>  * Advanced image usage (_sprite, animation, tile, font, parallax_)
>  * File I/O (_binary & XML reader & writer_)
>  * Utility classes (_Random, Conversions, Maths, File..._)
>  * Verbosity control


* #### __lionengine-core-awt__
>  * Engine implementation using __AWT__ from _JDK 8_


* #### __lionengine-game__
>  * Camera management (_view and movement_)
>  * Cursor (_synced or not to system pointer_)
>  * Background package (_for an easy background composition_)
>  * Tile based map package (_with minimap support, native save & load function_)
>    * Ray cast collision system
>    * Compatibility with raster bar effect
>    * A Star pathfinding implementation
>    * Tile extractor (_generate tilesheet from a level rip image_)
>    * Level rip converter (_generate a level data file from a tile sheet and a level rip image_)
>  * Object base (_support external XML configuration, trait system_)
>  * General object factory system (_create instance of object_)
>  * Objects handling system (_updating them, rendering, and retrieving_)
>  * Extensible _Trait_ system to compose object characteristics without code complexity
>    * Transformable (size and translation)
>    * Body (gravity handling)
>    * Launchable (launcher and projectile system)
>    * Rasterable (object raster bar effect)
>    * Producible (ability to produce other objects)
>    * Collidable (collision handling)
>    * and more...


* #### __lionengine-network__
>  * Server & Client system
>  * Customizable network message
>  * Integrated chat system


* #### __lionengine-audio-wav__
>  * Support for Wav sound


* #### __lionengine-audio-midi__
>  * Support for Midi music


* #### __lionengine-audio-sc68__
>  * Support for Sc68 Atari music (Sc68 wrapper)


* #### __lionengine-audio-adplug__
>  * Support for Loudness Sound music (AdPlug wrapper)


* #### __lionengine-audio-adlmidi__
>  * Support for Midi music (AdlMidi wrapper)


## Download

|[Go to website](http://www.b3dgs.com/v7/page.php?lang=en&section=lionengine)|[Last version](https://lionengine.b3dgs.com/v9-0/page.php?lang=en&section=downloads)|
|---|---|

## Installation

Steps to include the __LionEngine__ in your project:

1. Install at least the [Java JDK 8](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
2. Choose your favourite IDE ([Eclipse](http://www.eclipse.org/downloads/), [Netbeans](https://netbeans.org/downloads/)...)
3. Download the latest [LionEngine](http://lionengine.b3dgs.com/page.php?lang=en&section=downloads)
4. Include all __LionEngine__ libraries you need for your project, following the tree dependency:
   * __lionengine-core__ _(minimum requirement)_
     * __lionengine-core-awt__ _(uses_ __AWT__ _as graphic renderer, target for computer)_
     * __lionengine-game__ _(base for game development)_
     * __lionengine-network__ _(support for network)_
     * __lionengine-audio-wav__ _(support for Wav sound)_
     * __lionengine-audio-midi__ _(support for Midi music)_
     * __lionengine-audio-sc68__ _(support for Sc68 Atari music)_
     * __lionengine-audio-adplug__ _(support for LDS music)_
5. You are now ready to use the __LionEngine__ in your project

## Getting Started

Once you installed the __LionEngine__ in your project, you may would like to know how to prepare a quick sample as a first try:

#### Main class

```java
public class AppSamplePc
{
    public static void main(String[] args)
    {
        EngineAwt.start("Sample Project", Version.create(0, 1, 0), AppSamplePc.class);
        Loader.start(Config.windowed(Scene.NATIVE.get2x()), Scene.class);
    }
}
```

#### Minimal sequence

```java
public class Scene extends Sequence
{
    private static final Resolution NATIVE = new Resolution(320, 240, 60);

    public Scene(Context context)
    {
        super(context, NATIVE);
    }

    @Override
    public void load()
    {
        // Load resources
    }

    @Override
    public void update(double extrp)
    {
        // Update game
    }

    @Override
    public void render(Graphic g)
    {
        // Render game
    }
}
```

## Tutorials

* [LionEngine WebSite](http://lionengine.b3dgs.com)