package com.b3dgs.lionengine;

import java.awt.Graphics2D;
import java.awt.Transparency;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.lang.Thread.State;

import com.b3dgs.lionengine.input.Keyboard;
import com.b3dgs.lionengine.input.Mouse;
import com.b3dgs.lionengine.utility.UtilityImage;

/**
 * Sequence class is used for each derived sequence, such as Introduction, Menu, Scene... It contains a reference to the
 * screen used, the current configuration, input references ({@link Keyboard}, {@link Mouse}), and it includes a
 * standard game loop ({@link Sequence#update(double)} and {@link Sequence#render(Graphic)}), synchronised to a
 * specified frame rate.
 * <p>
 * Here a blank sequence implementation:
 * </p>
 * 
 * <pre>
 * public class MySequence
 *         extends Sequence
 * {
 *     public MySequence(Loader loader)
 *     {
 *         super(loader);
 *         // Initialize variables here
 *     }
 * 
 *     &#064;Override
 *     protected void load()
 *     {
 *         // Load resources here
 *     }
 * 
 *     &#064;Override
 *     protected void update(double extrp)
 *     {
 *         // Update routine
 *     }
 * 
 *     &#064;Override
 *     protected void render(Graphics g)
 *     {
 *         // Render routine
 *     }
 * }
 * 
 * </pre>
 */
public abstract class Sequence
{
    /** Error message internal. */
    private static final String MESSAGE_ERROR_LOADER = "Loader must not be null !";
    /** One nano second. */
    private static final long TIME_LONG = 1000000000L;
    /** One nano second. */
    private static final double TIME_DOUBLE = 1000000000.0;
    /** One millisecond. */
    private static final long TIME_INT = 1000000L;
    /** Extrapolation standard. */
    private static final double EXTRP = 1.0;

    /** Config reference. */
    public final Config config;
    /** Internal display reference. */
    public final Resolution source;
    /** Keyboard reference. */
    public final Keyboard keyboard;
    /** Mouse reference. */
    public final Mouse mouse;
    /** Loader reference. */
    protected final Loader loader;
    /** Screen width. */
    protected final int width;
    /** Screen height. */
    protected final int height;
    /** Screen reference. */
    private final Screen screen;
    /** External display reference. */
    private final Resolution output;
    /** Filter reference. */
    private final Filter filter;
    /** Hq3x use flag. */
    private final int hqx;
    /** Filter graphic. */
    private final Graphic graphic;
    /** Image buffer. */
    private final BufferedImage buf;
    /** Graphic buffer. */
    private final Graphics2D gbuf;
    /** Loop time for desired rate. */
    private final long frameDelay;
    /** Sequence thread. */
    private final SequenceThread thread;
    /** Has sync. */
    private final boolean sync;
    /** Filter used. */
    private final AffineTransformOp op;
    /** Direct rendering. */
    private final boolean directRendering;
    /** Previous sequence pointer. */
    private Sequence previousSequence;
    /** Next sequence pointer. */
    private Sequence nextSequence;
    /** Extrapolation flag. */
    private boolean extrapolated;
    /** Current frame rate. */
    private double currentFrameRate;
    /** Thread running flag. */
    private boolean isRunning;
    /** Will stop flag. */
    private boolean willStop;

    /**
     * Constructor.
     * 
     * @param loader The loader reference.
     * @param newSource The resolution source reference.
     */
    public Sequence(final Loader loader, Resolution newSource)
    {
        Check.notNull(loader, Sequence.MESSAGE_ERROR_LOADER);

        // Initialize
        this.loader = loader;
        config = loader.getConfig();
        config.setSource(newSource);
        screen = loader.getScreen();
        output = config.getOutput();
        source = config.getSource();
        keyboard = loader.getKeyboard();
        mouse = loader.getMouse();
        mouse.setConfig(config);
        filter = config.getFilter();
        sync = config.isWindowed() && output.getRate() > 0;
        graphic = new Graphic();
        extrapolated = true;

        // Time needed for a loop to reach desired rate
        if (output.getRate() == 0)
        {
            frameDelay = 0;
        }
        else
        {
            frameDelay = Sequence.TIME_LONG / output.getRate();
        }

        // Scale factor
        final double scaleX = output.getWidth() / (double) this.source.getWidth();
        final double scaleY = output.getHeight() / (double) this.source.getHeight();
        final AffineTransform tx = new AffineTransform();
        tx.scale(scaleX, scaleY);

        // Filter level
        switch (filter)
        {
            case HQ2X:
                hqx = 2;
                break;
            case HQ3X:
                hqx = 3;
                break;
            default:
                hqx = 0;
                break;
        }

        // Store internal size
        width = this.source.getWidth();
        height = this.source.getHeight();

        // Standard rendering
        if (this.source.getWidth() == output.getWidth() && this.source.getHeight() == output.getHeight())
        {
            buf = null;
            gbuf = null;
            op = null;
        }
        // Scaled rendering
        else
        {
            buf = UtilityImage.createBufferedImage(width, height, Transparency.OPAQUE);
            gbuf = buf.createGraphics();
            if (hqx > 1 || filter == Filter.NONE)
            {
                op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
            }
            else
            {
                op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
            }
            UtilityImage.optimizeGraphicsSpeed(gbuf);
        }
        graphic.setGraphics(gbuf);
        directRendering = hqx == 0 && (op == null || buf == null);
        thread = new SequenceThread(this);
    }

    /**
     * Loading sequence data.
     */
    protected abstract void load();

    /**
     * Update sequence.
     * 
     * @param extrp The extrapolation value. Can be used to have an non dependent machine speed calculation. Example: x
     *            += 5.0 * extrp
     */
    protected abstract void update(final double extrp);

    /**
     * Render sequence.
     * 
     * @param g The graphic output.
     */
    protected abstract void render(final Graphic g);

    /**
     * Terminate sequence, close screen, and start launcher if exists.
     */
    public final void end()
    {
        end(nextSequence);
    }

    /**
     * Terminate sequence, and set the next sequence.
     * 
     * @param nextSequence The next sequence reference.
     */
    public final void end(final Sequence nextSequence)
    {
        this.nextSequence = nextSequence;
        isRunning = false;
        willStop = true;
    }

    /**
     * Start the next sequence, call the {@link #load()} function, and wait for current sequence to end before next
     * sequence continues. This function should be used to synchronize two sequences (eg: load a next sequence while
     * being in a menu). Do not forget to call {@link #end()} in order to give control to the next sequence. The next
     * sequence should override {@link #onLoaded(double, Graphic)} for special load just before enter in the loop.
     * 
     * @param nextSequence The next sequence reference.
     * @param wait <code>true</code> to wait for the next sequence to be loaded, <code>false</code> else.
     */
    public final void start(final Sequence nextSequence, boolean wait)
    {
        this.nextSequence = nextSequence;
        nextSequence.previousSequence = this;
        nextSequence.setTitle(nextSequence.getClass().getName());
        nextSequence.start();
        if (wait)
        {
            synchronized (nextSequence)
            {
                try
                {
                    nextSequence.wait();
                }
                catch (final InterruptedException exception)
                {
                    Verbose.exception(Sequence.class, "start", exception);
                }
            }
        }
    }

    /**
     * Add a key listener.
     * 
     * @param listener The listener to add.
     */
    public final void addKeyListener(KeyListener listener)
    {
        screen.addKeyListener(listener);
    }

    /**
     * Set the mouse visibility.
     * 
     * @param visible The visibility state.
     */
    public final void setMouseVisible(boolean visible)
    {
        if (visible)
        {
            screen.showCursor();
        }
        else
        {
            screen.hideCursor();
        }
    }

    /**
     * Set the extrapolation flag.
     * 
     * @param extrapolated <code>true</code> will activate it, <code>false</code> will disable it.
     */
    public final void setExtrapolated(boolean extrapolated)
    {
        this.extrapolated = extrapolated;
    }

    /**
     * Get current frame rate (number of image per second).
     * 
     * @return The current number of image per second.
     */
    public final int getFps()
    {
        return (int) currentFrameRate;
    }

    /**
     * Clear the screen.
     * 
     * @param g The graphics output.
     */
    protected final void clearScreen(Graphic g)
    {
        g.clear(source);
    }

    /**
     * Called when the sequence has been loaded.
     * 
     * @param extrp The extrapolation value.
     * @param g The graphic output.
     */
    protected void onLoaded(double extrp, Graphic g)
    {
        // Nothing by default
    }

    /**
     * Called when sequence is closing. Should be used in case on special loading (such as music starting) when
     * {@link #start(Sequence, boolean)} has been used by another sequence.
     * 
     * @param hasNextSequence <code>true</code> if there is a next sequence, <code>false</code> else (then application
     *            will end definitely).
     */
    protected void onTerminate(boolean hasNextSequence)
    {
        // Nothing by default
    }

    /**
     * Called when sequence is focused (screen). Does nothing by default.
     */
    protected void onFocusGained()
    {
        // Nothing by default
    }

    /**
     * Called when sequence lost focus (screen). Does nothing by default.
     */
    protected void onLostFocus()
    {
        // Nothing by default
    }

    /**
     * Start sequence.
     */
    final void start()
    {
        thread.start();
    }

    /**
     * Run sequence.
     */
    final void run()
    {
        // Load sequence
        load();

        // Check previous sequence
        if (previousSequence != null)
        {
            notifyPreviousSequenceAndWait();
        }

        // Prepare sequence to be started
        double extrp = Sequence.EXTRP;
        long updateFpsTimer = 0L;
        currentFrameRate = output.getRate();
        screen.requestFocus();
        final int mcx = screen.getLocationX() + output.getWidth() / 2;
        final int mcy = screen.getLocationY() + output.getHeight() / 2;
        mouse.setCenter(mcx, mcy);
        onLoaded(extrp, screen.getGraphic());

        // Main loop
        isRunning = true;
        while (isRunning)
        {
            final long lastTime = System.nanoTime();
            mouse.update();
            update(extrp);
            preRender(screen.getGraphic());
            screen.update();
            sync(System.nanoTime() - lastTime);

            // Perform extrapolation and frame rate calculation
            final long currentTime = System.nanoTime();
            if (extrapolated)
            {
                extrp = source.getRate() / Sequence.TIME_DOUBLE * (currentTime - lastTime);
            }
            else
            {
                extrp = Sequence.EXTRP;
            }
            if (currentTime - updateFpsTimer > Sequence.TIME_LONG)
            {
                currentFrameRate = Sequence.TIME_DOUBLE / (currentTime - lastTime);
                updateFpsTimer = currentTime;
            }
            if (willStop && nextSequence != null)
            {
                waitForNextSequenceWaiting();
            }
        }
        onTerminate(nextSequence != null);
        synchronized (this)
        {
            notifyAll();
        }
    }

    /**
     * Set thread title.
     * 
     * @param title The title.
     */
    final void setTitle(String title)
    {
        thread.setName(title);
    }

    /**
     * Get next sequence.
     * 
     * @return The next sequence.
     */
    final Sequence getNextSequence()
    {
        return nextSequence;
    }

    /**
     * Get the started state.
     * 
     * @return <code>true</code> if started, <code>false</code> else.
     */
    final boolean isStarted()
    {
        return thread.isAlive();
    }

    /**
     * Notify the previous sequence and wait for its termination.
     */
    private void notifyPreviousSequenceAndWait()
    {
        synchronized (this)
        {
            notify();
        }
        synchronized (previousSequence)
        {
            try
            {
                previousSequence.wait();
            }
            catch (final InterruptedException exception)
            {
                Verbose.exception(Sequence.class, "run", exception);
            }
        }
    }

    /**
     * Active wait for the next sequence to wait for this sequence before allow ending.
     */
    private void waitForNextSequenceWaiting()
    {
        if (!isRunning && nextSequence.isStarted())
        {
            isRunning = true;
        }
        if (nextSequence.thread.getState() == State.WAITING)
        {
            isRunning = false;
        }
    }

    /**
     * Render handler.
     * 
     * @param g The graphic output.
     */
    private void preRender(final Graphic g)
    {
        if (directRendering)
        {
            // Direct rendering
            render(g);
        }
        else
        {
            // Intermediate rendering (use of filter)
            render(graphic);
            switch (hqx)
            {
                case 2:
                    g.drawImage(new Hq2x(buf).getScaledImage(), 0, 0);
                    break;
                case 3:
                    g.drawImage(new Hq3x(buf).getScaledImage(), 0, 0);
                    break;
                default:
                    g.drawImage(buf, op, 0, 0);
                    break;
            }
        }
    }

    /**
     * Sync frame rate to desired if possible.
     * 
     * @param time The update tile.
     */
    private void sync(final long time)
    {
        // Sync only if windowed and has a desired rate
        if (sync)
        {
            try
            {
                // Time to wait
                final long wait = frameDelay - time;

                // Need to wait
                if (wait > 0)
                {
                    Thread.sleep(wait / Sequence.TIME_INT, (int) (wait % Sequence.TIME_INT));
                }
            }
            catch (final InterruptedException exception)
            {
                Verbose.exception(Sequence.class, "sync", exception);
            }
        }
    }
}
