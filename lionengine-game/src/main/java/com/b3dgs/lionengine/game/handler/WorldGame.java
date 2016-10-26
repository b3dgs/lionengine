/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.handler;

import java.io.IOException;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.core.Config;
import com.b3dgs.lionengine.core.Context;
import com.b3dgs.lionengine.core.InputDevice;
import com.b3dgs.lionengine.core.Resolution;
import com.b3dgs.lionengine.game.camera.Camera;
import com.b3dgs.lionengine.game.feature.Factory;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Renderable;
import com.b3dgs.lionengine.stream.FileReading;
import com.b3dgs.lionengine.stream.FileWriting;
import com.b3dgs.lionengine.stream.Stream;
import com.b3dgs.lionengine.util.UtilStream;

/**
 * Default world model, designed to contain game elements ({@link Factory}, {@link Handler} ...).
 * <p>
 * It contains the following configured fields:
 * </p>
 * <ul>
 * <li>{@link Config} : The configuration used by the {@link com.b3dgs.lionengine.core.Loader}</li>
 * <li><code>width</code>: The source screen width, retrieve from the source screen {@link Resolution}</li>
 * <li><code>height</code>: The source screen height, retrieve from the source screen {@link Resolution}</li>
 * <li>{@link Services}: Pre-configured instance with the following added services:
 * <ul>
 * <li>{@link Camera}: Configured with screen size as view</li>
 * <li>{@link Factory}</li>
 * <li>{@link Handler}: Shipped with {@link ComponentRefreshable} and {@link ComponentDisplayable}</li>
 * </ul>
 * </li>
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
 *     public MySequence(Context context)
 *     {
 *         super(loader);
 *         // Initialize variables here
 *         world = new World(context);
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
 */
public abstract class WorldGame implements Updatable, Renderable
{
    /** Services instance. */
    protected final Services services = new Services();
    /** Camera instance (Configured with screen size as view). */
    protected final Camera camera = services.create(Camera.class);
    /** Factory instance. */
    protected final Factory factory = services.create(Factory.class);
    /** Handler instance (configured with {@link ComponentRefreshable} and {@link ComponentDisplayable}). */
    protected final Handler handler = services.create(Handler.class);
    /** Config reference. */
    protected final Config config;
    /** Internal display reference. */
    protected final Resolution source;
    /** External display reference. */
    protected final Resolution output;
    /** Context reference. */
    private final Context context;

    /**
     * Create a new world. The sequence given by reference allows to retrieve essential data such as {@link Config},
     * screen size and wide state.
     * 
     * @param context The context reference.
     */
    public WorldGame(Context context)
    {
        this.context = context;
        config = context.getConfig();
        source = config.getSource();
        output = config.getOutput();
        camera.setView(0, 0, source.getWidth(), source.getHeight(), source.getHeight());
        handler.addComponent(new ComponentRefreshable());
        handler.addComponent(new ComponentDisplayable());
    }

    /**
     * Internal world saves; called from {@link #saveToFile(Media)} function. The world will be saved in a file
     * as binary. Here should be called all saving functions, such as
     * {@link com.b3dgs.lionengine.game.map.feature.persister.MapTilePersister#save(FileWriting)}...
     * 
     * @param file The file writer reference.
     * @throws IOException If error on writing.
     */
    protected abstract void saving(FileWriting file) throws IOException;

    /**
     * Internal world loads; called from {@link #loadFromFile(Media)} function. The world will be loaded from
     * an existing binary file. Here should be called all loading functions, such as
     * {@link com.b3dgs.lionengine.game.map.feature.persister.MapTilePersister#load(FileReading)}...
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
            UtilStream.safeClose(writing);
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
            UtilStream.safeClose(reading);
        }
    }

    /**
     * Fill with color.
     * 
     * @param g The graphic output.
     * @param color The color to fill with.
     */
    public void fill(Graphic g, ColorRgba color)
    {
        g.setColor(color);
        g.drawRect(0, 0, source.getWidth(), source.getHeight(), true);
    }

    /**
     * Get the input device instance from its type.
     * 
     * @param <T> The input device.
     * @param type The input device type.
     * @return The input instance reference.
     * @throws LionEngineException If device not found.
     */
    public final <T extends InputDevice> T getInputDevice(Class<T> type)
    {
        return context.getInputDevice(type);
    }

    /*
     * Updatable
     */

    /**
     * {@inheritDoc}
     * By default, updates handler.
     */
    @Override
    public void update(double extrp)
    {
        handler.update(extrp);
    }

    /*
     * Renderable
     */

    /**
     * {@inheritDoc}
     * By default, renders handler.
     */
    @Override
    public void render(Graphic g)
    {
        handler.render(g);
    }
}
