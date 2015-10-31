/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Renderable;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.UtilFile;
import com.b3dgs.lionengine.core.Config;
import com.b3dgs.lionengine.core.Resolution;
import com.b3dgs.lionengine.stream.FileReading;
import com.b3dgs.lionengine.stream.FileWriting;
import com.b3dgs.lionengine.stream.Stream;

/**
 * Default world model, designed to contain game elements ({@link com.b3dgs.lionengine.game.map.MapTile},
 * {@link com.b3dgs.lionengine.game.object.Handler}, {@link com.b3dgs.lionengine.game.object.Factory}...).
 * <p>
 * It contains different elements, such as:
 * </p>
 * <ul>
 * <li>{@link Config} : The configuration used by the {@link com.b3dgs.lionengine.core.Loader}</li>
 * <li><code>width</code> : The source screen width, retrieve from the source screen {@link Resolution}</li>
 * <li><code>height</code> : The source screen height, retrieve from the source screen {@link Resolution}</li>
 * </ul>
 * <p>
 * It has to be handled by a {@link com.b3dgs.lionengine.core.Sequence}. Here a standard world usage:
 * </p>
 * 
 * <pre>
 * public class MySequence extends Sequence
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
 *     public void load()
 *     {
 *         // Load resources here
 *         world.loadFromFile(Media.get(&quot;level.lvl&quot;));
 *     }
 * 
 *     &#064;Override
 *     public void update(double extrp)
 *     {
 *         // Update routine
 *         world.update(extrp);
 *     }
 * 
 *     &#064;Override
 *     public void render(Graphic g)
 *     {
 *         // Render routine
 *         world.render(g);
 *     }
 * }
 * </pre>
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public abstract class WorldGame implements Updatable, Renderable
{
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
     * @param config The config reference.
     */
    public WorldGame(Config config)
    {
        this.config = config;
        source = config.getSource();
        output = config.getOutput();
        width = source.getWidth();
        height = source.getHeight();
    }

    /**
     * Internal world saves; called from {@link WorldGame#saveToFile(Media)} function. The world will be saved in a file
     * as binary. Here should be called all saving functions, such as
     * {@link com.b3dgs.lionengine.game.map.MapTile#save(FileWriting)}...
     * 
     * @param file The file writer reference.
     * @throws IOException If error on writing.
     */
    protected abstract void saving(FileWriting file) throws IOException;

    /**
     * Internal world loads; called from {@link WorldGame#loadFromFile(Media)} function. The world will be loaded from
     * an existing binary file. Here should be called all loading functions, such as
     * {@link com.b3dgs.lionengine.game.map.MapTile#load(FileReading)}
     * ...
     * 
     * @param file The file reader reference.
     * @throws IOException If error on reading.
     */
    protected abstract void loading(FileReading file) throws IOException;

    /**
     * Save world to the specified file.
     * 
     * @param media The output media.
     * @throws LionEngineException If error on saving to file.
     */
    public final void saveToFile(Media media)
    {
        final FileWriting writing = Stream.createFileWriting(media);
        try
        {
            saving(writing);
        }
        catch (final IOException exception)
        {
            throw new LionEngineException(exception, media, "Error on saving to file !");
        }
        finally
        {
            UtilFile.safeClose(writing);
        }
    }

    /**
     * Load world from the specified file.
     * 
     * @param media The input media.
     * @throws LionEngineException If error on loading from file.
     */
    public final void loadFromFile(Media media)
    {
        final FileReading reading = Stream.createFileReading(media);
        try
        {
            loading(reading);
        }
        catch (final IOException exception)
        {
            throw new LionEngineException(exception, media, "Error on loading from file !");
        }
        finally
        {
            UtilFile.safeClose(reading);
        }
    }
}
