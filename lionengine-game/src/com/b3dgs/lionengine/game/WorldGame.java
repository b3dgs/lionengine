package com.b3dgs.lionengine.game;

import java.io.IOException;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Display;
import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Loader;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Ratio;
import com.b3dgs.lionengine.Sequence;
import com.b3dgs.lionengine.file.File;
import com.b3dgs.lionengine.file.FileReading;
import com.b3dgs.lionengine.file.FileWriting;
import com.b3dgs.lionengine.game.entity.HandlerEntityGame;
import com.b3dgs.lionengine.game.maptile.MapTile;
import com.b3dgs.lionengine.game.maptile.MapTileGame;
import com.b3dgs.lionengine.input.Keyboard;
import com.b3dgs.lionengine.input.Mouse;

/**
 * Default world model, designed to contain game elements ({@link MapTile}, {@link HandlerEntityGame},
 * {@link FactoryGame}...).
 * <p>
 * It contains different elements, such as:
 * <ul>
 * <li>{@link Keyboard} : The keyboard input reference, in order to retrieve keyboard key uses</li>
 * <li>{@link Mouse} : The mouse input reference, in order to retrieve mouse movement and click uses</li>
 * <li>{@link Config} : The configuration used by the {@link Loader}</li>
 * <li><code>width</code> : The internal screen width, retrieve from the internal screen {@link Display}</li>
 * <li><code>height</code> : The internal screen height, retrieve from the internal screen {@link Display}</li>
 * <li><code>wide</code> : The screen wide flag, where <code>true</code> indicate a large screen</li>
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
 * 
 *     &#064;Override
 *     protected void onTerminate()
 *     {
 *         // Called when sequence is closing, optional
 *     }
 * }
 * </pre>
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
    protected final Display internal;
    /** External display reference. */
    protected final Display display;
    /** Screen size width. */
    protected final int width;
    /** Screen size height. */
    protected final int height;
    /** True if screen is wide. */
    protected final boolean wide;

    /**
     * Create a new world. The sequence given by reference allows to retrieve essential data such as {@link Config},
     * screen size and wide state.
     * 
     * @param sequence The sequence reference.
     */
    public WorldGame(final Sequence sequence)
    {
        keyboard = sequence.keyboard;
        mouse = sequence.mouse;
        config = sequence.config;
        internal = sequence.config.internal;
        display = sequence.config.external;

        // Check wide state
        if (Ratio.R16_9 == config.external.getRatio() && Ratio.R16_10 == config.external.getRatio())
        {
            wide = true;
        }
        else
        {
            wide = false;
        }
        // Retrieve screen size
        width = sequence.config.internal.getWidth();
        height = sequence.config.internal.getHeight();
    }

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
        loaded();
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
     * Called when world is loaded.
     */
    protected abstract void loaded();
}
