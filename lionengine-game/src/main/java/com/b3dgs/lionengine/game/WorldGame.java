/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.b3dgs.lionengine.game;

import java.io.IOException;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Keyboard;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Mouse;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.core.Config;
import com.b3dgs.lionengine.core.Loader;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.Sequence;
import com.b3dgs.lionengine.file.File;
import com.b3dgs.lionengine.file.FileReading;
import com.b3dgs.lionengine.file.FileWriting;
import com.b3dgs.lionengine.game.entity.HandlerEntityGame;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.map.MapTileGame;

/**
 * Default world model, designed to contain game elements ({@link MapTile}, {@link HandlerEntityGame},
 * {@link FactoryObjectGame}...).
 * <p>
 * It contains different elements, such as:
 * <ul>
 * <li>{@link Keyboard} : The keyboard input reference, in order to retrieve keyboard key uses</li>
 * <li>{@link Mouse} : The mouse input reference, in order to retrieve mouse movement and click uses</li>
 * <li>{@link Config} : The configuration used by the {@link Loader}</li>
 * <li><code>width</code> : The source screen width, retrieve from the source screen {@link Resolution}</li>
 * <li><code>height</code> : The source screen height, retrieve from the source screen {@link Resolution}</li>
 * </ul>
 * </p>
 * It has to be handled by a {@link Sequence}.
 * <p>
 * Here a standard world use:
 * </p>
 * 
 * <pre>
 * public class MySequence
 *         extends Sequence
 * {
 *     private final World world;
 * 
 *     public MySequence(Loader loader)
 *     {
 *         super(loader);
 *         // Initialize variables here
 *         world = new World(this);
 *     }
 * 
 *     &#064;Override
 *     protected void load()
 *     {
 *         // Load resources here
 *         world.loadFromFile(Media.get(&quot;level.lvl&quot;));
 *     }
 * 
 *     &#064;Override
 *     protected void update(double extrp)
 *     {
 *         // Update routine
 *         world.update(extrp);
 *     }
 * 
 *     &#064;Override
 *     protected void render(Graphic g)
 *     {
 *         // Render routine
 *         world.render(g);
 *     }
 * }
 * </pre>
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public abstract class WorldGame
{
    /** Keyboard reference. */
    protected final Keyboard keyboard;
    /** Mouse reference. */
    protected final Mouse mouse;
    /** Config reference. */
    protected final Config config;
    /** Internal display reference. */
    protected final Resolution source;
    /** External display reference. */
    protected final Resolution output;
    /** Screen size width. */
    protected final int width;
    /** Screen size height. */
    protected final int height;

    /**
     * Create a new world. The sequence given by reference allows to retrieve essential data such as {@link Config},
     * screen size and wide state.
     * 
     * @param sequence The sequence reference.
     */
    public WorldGame(final Sequence sequence)
    {
        keyboard = sequence.getKeyboard();
        mouse = sequence.getMouse();
        config = sequence.getConfig();
        source = config.getSource();
        output = config.getOutput();
        width = source.getWidth();
        height = source.getHeight();
    }

    /**
     * Internal world updates.
     * 
     * @param extrp The extrapolation value.
     */
    public abstract void update(final double extrp);

    /**
     * Internal world rendering.
     * 
     * @param g The graphic output.
     */
    public abstract void render(final Graphic g);

    /**
     * Internal world saves; called from {@link WorldGame#saveToFile(Media)} function. The world will be saved in a file
     * as binary. Here should be called all saving functions, such as {@link MapTileGame#save(FileWriting)}...
     * 
     * @param file The file writer reference.
     * @throws IOException If error on writing.
     */
    protected abstract void saving(FileWriting file) throws IOException;

    /**
     * Internal world loads; called from {@link WorldGame#loadFromFile(Media)} function. The world will be loaded from
     * an existing binary file. Here should be called all loading functions, such as
     * {@link MapTileGame#load(FileReading)} ...
     * 
     * @param file The file reader reference.
     * @throws IOException If error on reading.
     */
    protected abstract void loading(FileReading file) throws IOException;

    /**
     * Save world to the specified file.
     * 
     * @param media The output media.
     */
    public final void saveToFile(Media media)
    {
        try (FileWriting writing = File.createFileWriting(media);)
        {
            saving(writing);
        }
        catch (final IOException exception)
        {
            throw new LionEngineException(exception, media, "Error on saving to file !");
        }
    }

    /**
     * Load world from the specified file.
     * 
     * @param media The input media.
     */
    public final void loadFromFile(Media media)
    {
        try (FileReading reading = File.createFileReading(media);)
        {
            loading(reading);
        }
        catch (final IOException exception)
        {
            throw new LionEngineException(exception, media, "Error on loading from file !");
        }
    }
}
