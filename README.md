|<img hspace="90"/><br>[![Build](https://github.com/b3dgs/lionengine/actions/workflows/build.yml/badge.svg?branch=master)](https://github.com/b3dgs/lionengine/actions/workflows/build.yml)<br>[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=b3dgs_lionengine&metric=coverage)](https://sonarcloud.io/summary/new_code?id=b3dgs_lionengine)<br>[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=b3dgs_lionengine&metric=ncloc)](https://sonarcloud.io/summary/new_code?id=b3dgs_lionengine)<br>[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.b3dgs.lionengine/lionengine-core/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.b3dgs.lionengine/lionengine-core)<br>[![Dev](https://img.shields.io/badge/dev-v10.0.0%20SNAPSHOT-yellow.svg)](https://github.com/b3dgs/lionengine/milestone/34)<br>[![License](https://img.shields.io/github/license/b3dgs/lionengine)](/LICENSE)<br><img hspace="90"/> | <a href="https://www.b3dgs.com/v7/page.php?lang=en&section=lionengine"><img hspace="90" src="https://user-images.githubusercontent.com/34600369/41530953-b6f4554a-72e9-11e8-9ab1-e49d390a9117.png" width="200"/></a> | <img hspace="90"/><br>[Presentation](#presentation)<br>[General features](#general-features)<br>[Download](#download)<br>[Installation](#installation)<br>[Getting Started](#getting-started)<br>[Tutorials](#tutorials)<br><img hspace="90"/> |
|:---|:---:|---:|

## Presentation
The __LionEngine__ is a game engine especially developed during the project [Lionheart Remake](https://lionheart.b3dgs.com) for an easy Java use.
The engine is as a library, in Jar format (_including its javadoc_), which can be included in any project;
for utility class uses, or to directly implement and inherit a game skeleton (_including management of frame rate, extrapolation, input output..._).

Using Java 17 internal libraries, it is specifically designed for 2D games (no support for 3D at the moment), and proposes a set of functions for 2D resources management (_images_, _sprites_, _animations_, _tiles_, _font_...).
Inputs and outputs are also available, with an easy keys retrieval, mouse movement... Management of music file are also available (_Wav_, _Midi_, and more using plug-ins, such as _Sc68_, _AdPlug_ and _AdlMidi_).
Windowed, full-screen and applet formats are fully supported, with a complete frame rate control.

It supports __Android 5.0__ *(API 21)*.
The only change to perform is the gameplay part, as the '__mouse__' and '__keyboard__' concepts are different on Android.
Everything else is fully compatible and does not require any changes.

The __LionEngine Editor__ is designed to help game development by providing a set of tool for general object edition and map generation.

## General Features
### Core
> * #### __lionengine-core__
>   * Simple initialization and screen configuration (_windowed, fullscreen_)
>   * Advanced game loop (_machine speed independent, frame skipping, hybrid_)
>   * Filtering capability (_Bilinear, Blur, HQ2X, HQ3X, Scanline, CRT_)
>   * Sequence control (_intro, menu, game part, credits..._)
>   * Easy resource management (_relative to resource directory, JAR, temp_)
>   * Advanced image usage (_sprite, animation, tile, font, parallax_)
>   * File I/O (_binary & XML reader & writer_)
>   * Server & Client UDP system
>   * Utility classes (_Random, Conversions, Maths, File..._)
>   * Verbosity control
> * #### __lionengine-core-awt__
>   * Engine implementation using __AWT__ from _JDK 17_
> * #### __lionengine-core-android__
>   * Engine implementation using __Android 5.0__ _as graphic renderer, target for smartphones_
> * #### __lionengine-game__
>   * Camera management (_view and movement_)
>   * Cursor (_synced or not to system pointer_)
>   * Background package (_for an easy background composition_)
>   * Tile based map package (_with minimap support, native save & load function_)
>     * Ray cast collision system
>     * Compatibility with raster bar effect
>     * A Star pathfinding implementation
>     * Tile extractor (_generate tilesheet from a level rip image_)
>     * Level rip converter (_generate a level data file from a tile sheet and a level rip image_)
>   * Object package
>     * Setup (_external XML configuration_)
>     * Factory (_reusable cached object instances_)
>     * Handler (_updating, rendering, and retrieving_)
>   * Extensible _Feature_ system to compose object characteristics without code complexity
>     * Transformable (_size and translation_)
>     * Body (_gravity handling_)
>     * Launchable (_launcher and projectile system_)
>     * Rasterable (_object raster bar effect_)
>     * Producible (_ability to produce other objects_)
>     * Collidable (_collision handling_)
>     * Networkable (_synced object over network_)
>     * and more...
> * #### __lionengine-audio-wav__
>   * Support for Wav sound
> * #### __lionengine-audio-sc6__
>   * Support for Sc68 Atari music (Sc68 wrapper)
> * #### __lionengine-audio-adplug__
>   * Support for Loudness Sound music (AdPlug wrapper)
> * #### __lionengine-audio-adlmidi__
>   * Support for Midi music (AdlMidi wrapper)
### Editor
> * #### __com.b3dgs.lionengine.core.swt__
>   * Core implementation with SWT, dedicated to an Eclipse RCP4 usage
> * #### __com.b3dgs.lionengine.editor__
>   * Complete standalone editor which can be used in any project for general level edition
>   * Can be extended to perform more specific things
> * #### __com.b3dgs.lionengine.editor.map__
>   * Full map tile support with import and export function
> * #### __com.b3dgs.lionengine.editor.object__
>   * Object handling and editing with advanced properties
>   * Can be placed and removed on map with mouse
> * #### __com.b3dgs.lionengine.editor.animation__
>   * Animation editor frame by frame with mouse
> * #### __com.b3dgs.lionengine.editor.collision__
>   * Collision edition assignment with mouse
> * #### __com.b3dgs.lionengine.editor.pathfinding__
>   * Pathfinding support with properties edition

## Download
|[Go to website](https://www.b3dgs.com/v7/page.php?lang=en&section=lionengine)|[Last version](https://lionengine.b3dgs.com/v9-0/page.php?lang=en&section=downloads)|[Maven Central](https://search.maven.org/artifact/com.b3dgs.lionengine/lionengine-core/10.0.0-SNAPSHOT/jar)
|---|---|---|

## Installation
Steps to include the __LionEngine__ in your project:
1. Install at least the [Java JDK 17](https://adoptium.net/)
2. Choose your favourite IDE ([Eclipse](https://www.eclipse.org/downloads/), [Netbeans](https://netbeans.apache.org/download/)...)
3. Download the latest [LionEngine](https://search.maven.org/search?q=com.b3dgs.lionengine)
4. Include all __LionEngine__ libraries you need for your project, following the tree dependency:
   * __lionengine-core__ _(minimum requirement)_
     * __lionengine-core-awt__ _(uses_ __AWT__ _as graphic renderer, target for computer)_
     * __lionengine-game__ _(base for game development)_
     * __lionengine-audio-wav__ _(support for Wav sound)_
     * __lionengine-audio-sc68__ _(support for Sc68 Atari music)_
     * __lionengine-audio-adplug__ _(support for LDS music)_
     * __lionengine-audio-adlmidi__ _(support for Midi music)_
5. You are now ready to use the __LionEngine__ in your project

## Getting Started
Once you installed the __LionEngine__ in your project, you may would like to know how to prepare a quick sample as a first try:

#### Main class
* Using __lionengine-core-awt__
```java
public class AppSamplePc
{
    public static void main(String[] args)
    {
        EngineAwt.start("Sample Project", new Version(0, 1, 0), AppSamplePc.class);
        Loader.start(Config.windowed(Scene.NATIVE.get2x()), Scene.class);
    }
}
```

* Using __lionengine-core-android__
```java
public class ActivitySample extends ActivityGame
{
    @Override
    protected void start(Bundle bundle)
    {
        EngineAndroid.start("Sample Project", new Version(0, 1, 0), this);
        Loader.start(Config.fullscreen(Scene.NATIVE), Scene.class);
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
* [LionEngine WebSite](https://lionengine.b3dgs.com)
