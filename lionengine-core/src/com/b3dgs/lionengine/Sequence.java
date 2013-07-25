package com.b3dgs.lionengine;

import java.awt.Graphics2D;
import java.awt.Transparency;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

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
    /** True if screen is wide. */
    public final boolean wide;
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
    /** Internal display reference. */
    private final Display internal;
    /** External display reference. */
    private final Display external;
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
    /** Next sequence pointer. */
    private Sequence nextSequence;
    /** Extrapolation flag. */
    private boolean extrapolated;
    /** Current frame rate. */
    private double currentFrameRate;
    /** Thread running flag. */
    private boolean isRunning;

    /**
     * Create a new sequence.
     * 
     * @param loader The loader reference.
     */
    public Sequence(final Loader loader)
    {
        Check.notNull(loader, Sequence.MESSAGE_ERROR_LOADER);

        // Initialize
        this.loader = loader;
        screen = loader.getScreen();
        config = screen.config;
        internal = screen.config.internal;
        external = screen.config.external;
        keyboard = loader.getKeyboard();
        mouse = loader.getMouse();
        filter = config.filter;
        sync = config.isWindowed() && external.getRate() > 0;
        graphic = new Graphic();
        extrapolated = false;

        // Time needed for a loop to reach desired rate
        if (external.getRate() == 0)
        {
            frameDelay = 0;
        }
        else
        {
            frameDelay = Sequence.TIME_LONG / external.getRate();
        }

        // Scale factor
        final double scaleX = external.getWidth() / (double) internal.getWidth();
        final double scaleY = external.getHeight() / (double) internal.getHeight();
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
        width = internal.getWidth();
        height = internal.getHeight();
        wide = screen.isWide();

        // Standard rendering
        if (internal.getWidth() == external.getWidth() && internal.getHeight() == external.getHeight())
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
            if (hqx > 1)
            {
                op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
            }
            else
            {
                op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
            }
            UtilityImage.optimizeGraphicsSpeed(gbuf);
        }
        graphic.setGraphics(gbuf);
        directRendering = hqx == 0 && (op == null || buf == null);
        thread = new SequenceThread(this);
    }

    /**
     * Terminate sequence, close screen, and start launcher if exists.
     */
    public final void end()
    {
        nextSequence = null;
        isRunning = false;
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
     * Add a key listener.
     * 
     * @param listener The listener to add.
     */
    public final void addKeyListener(KeyListener listener)
    {
        screen.addKeyListener(listener);
    }

    /**
     * Set the extrapolation flag.
     * 
     * @param extrapolated <code>true</code> will activate it, <code>false</code> will disable it.
     */
    protected final void setExtrapolated(boolean extrapolated)
    {
        this.extrapolated = extrapolated;
    }

    /**
     * Clear the screen.
     * 
     * @param g The graphics output.
     */
    protected final void clearScreen(Graphic g)
    {
        g.clear(internal);
    }

    /**
     * Loading sequence data.
     */
    protected abstract void load();

    /**
     * Update sequence.
     * 
     * @param extrp The extrapolation value.
     */
    protected abstract void update(final double extrp);

    /**
     * Render sequence.
     * 
     * @param g The graphic output.
     */
    protected abstract void render(final Graphic g);

    /**
     * Called when sequence is closing.
     */
    protected void onTerminate()
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
     * Wait for sequence ending.
     * 
     * @throws InterruptedException If error on join.
     */
    final void join() throws InterruptedException
    {
        thread.join();
    }

    /**
     * Run sequence.
     */
    final void run()
    {
        load();

        double extrp = Sequence.EXTRP;
        long updateFpsTimer = 0L;
        currentFrameRate = external.getRate();
        screen.requestFocus();
        mouse.setCenter(screen.getLocationX() + external.getWidth() / 2, screen.getLocationY() + external.getHeight()
                / 2);

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
                extrp = internal.getRate() / Sequence.TIME_DOUBLE * (currentTime - lastTime);
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
        }
        onTerminate();
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
                Verbose.warning(Sequence.class, "sync", exception.getMessage());
            }
        }
    }
}
