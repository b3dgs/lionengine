<div align="center">

# <img src="logo.png" height="240" alt="LionEngine"/>

**A Java 2D game engine, born from the [Lionheart Remake](https://lionheart.b3dgs.com) project.**

[![Build](https://github.com/b3dgs/lionengine/actions/workflows/ci-cd.yml/badge.svg?branch=master)](https://github.com/b3dgs/lionengine/actions/workflows/ci-cd.yml)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=b3dgs_lionengine&metric=coverage)](https://sonarcloud.io/summary/new_code?id=b3dgs_lionengine)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=b3dgs_lionengine&metric=ncloc)](https://sonarcloud.io/summary/new_code?id=b3dgs_lionengine)
[![Maven Central](https://maven-badges.sml.io/maven-central/com.b3dgs.lionengine/lionengine-core/badge.svg)](https://maven-badges.sml.io/maven-central/com.b3dgs.lionengine/lionengine-core)
[![Dev](https://img.shields.io/badge/dev-v11.0.0%20SNAPSHOT-yellow.svg)](https://github.com/b3dgs/lionengine/milestone/34)
[![License](https://img.shields.io/github/license/b3dgs/lionengine)](/LICENSE)

[🌐 Website](https://www.b3dgs.com/v7/page.php?lang=en&section=lionengine) · [📦 Download](https://lionengine.b3dgs.com/v9-0/page.php?lang=en&section=downloads) · [📚 Tutorials](https://lionengine.b3dgs.com) · [🔍 Maven Central](https://search.maven.org/search?q=com.b3dgs.lionengine)

</div>

---

## What is it?

The **LionEngine** is a lightweight, modular **2D game engine for Java**, distributed as a simple JAR library. Plug it into any project to get a full-featured foundation for your game — or use only the parts you need.

It was built for game development, powering [Lionheart Remake](https://lionheart.b3dgs.com), and designed to stay out of your way while giving you everything to ship a game.

**Targets:** Desktop (*Java >=17 + AWT*) and **Android 5.0** (*API 21*). The game logic is fully portable between both; only input handling differs.

---

## Features

### 🎮 Core (`lionengine-core`)
- Machine-speed-independent game loop with frame skipping and hybrid modes
- Windowed & fullscreen support with complete frame rate control
- Image filtering: Bilinear, Blur, HQ2X, HQ3X, Scanline, CRT
- Sequence-based flow (intro → menu → game → credits…)
- Resource management relative to directories, JARs, or temp
- Sprites, animations, tiles, fonts, parallax layers
- Binary & XML file I/O
- UDP Server/Client networking
- Utility classes: Random, Maths, Conversions, File…

### 🕹️ Game (`lionengine-game`)
- **Camera** with configurable view and movement
- **Cursor** — synced or independent from the system pointer
- **Tile-based map** with minimap, save/load, A* pathfinding, ray cast collisions, and raster bar effect
- **Tile tools**: extract tilesheet from a level rip, generate level data files
- **Object system** driven by XML configuration, factory caching, and a handler for update/render/retrieve
- **Composable Feature system** — add characteristics to objects without inheritance complexity:

| Feature | Description |
|---|---|
| `Transformable` | Size and translation |
| `Body` | Gravity handling |
| `Collidable` | Collision detection |
| `Launchable` | Launcher & projectile system |
| `Rasterable` | Raster bar visual effect |
| `Producible` | Object spawning capability |
| `Networkable` | Synchronized over network |

### 🔊 Audio (`lionengine-audio-*`)
| Module | Format |
|---|---|
| `lionengine-audio-wav` | WAV |
| `lionengine-audio-sc68` | Sc68 Atari music |
| `lionengine-audio-adplug` | LDS / AdPlug |
| `lionengine-audio-adlmidi` | MIDI via AdlMidi |

### 🛠️ LionEngine Editor
A standalone Eclipse RCP4 editor to speed up level design and object authoring:

- **Map editor** — import/export tile maps
- **Object editor** — place, remove, and configure objects on the map
- **Animation editor** — frame-by-frame editing with mouse
- **Collision editor** — assign collision shapes visually
- **Pathfinding editor** — configure pathfinding properties per tile

---

## Installation

**Prerequisites:** [Java JDK 17+](https://adoptium.net/)

Add LionEngine to your Maven project:

```xml
<dependency>
    <groupId>com.b3dgs.lionengine</groupId>
    <artifactId>lionengine-core</artifactId>
    <version>11.0.0-SNAPSHOT</version>
</dependency>
```

Or [download the JARs manually](https://search.maven.org/search?q=com.b3dgs.lionengine) and add them to your classpath.

**Dependency tree** — include only what you need:

```
lionengine-core                  ← required
├── lionengine-core-awt          ← desktop (AWT renderer)
├── lionengine-game              ← game logic
├── lionengine-audio-wav         ← WAV audio
├── lionengine-audio-sc68        ← Sc68 Atari music
├── lionengine-audio-adplug      ← LDS music
└── lionengine-audio-adlmidi     ← MIDI music
```

---

## Quick Start

### Desktop (AWT)

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

### Android

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

### Minimal Scene

```java
public class Scene extends Sequence
{
    static final Resolution NATIVE = new Resolution(320, 240, 60);

    public Scene(Context context)
    {
        super(context, NATIVE);
    }

    @Override
    public void load()
    {
        // Load your resources here
    }

    @Override
    public void update(double extrp)
    {
        // Update game logic
    }

    @Override
    public void render(Graphic g)
    {
        // Draw your frame
    }
}
```

---
