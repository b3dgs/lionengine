package com.b3dgs.lionengine;

import java.awt.AWTError;
import java.awt.AWTException;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.IllegalComponentStateException;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import com.b3dgs.lionengine.input.Keyboard;
import com.b3dgs.lionengine.input.Mouse;
import com.b3dgs.lionengine.utility.UtilityImage;

/**
 * Representation of the screen device, supporting fullscreen and windowed mode. Screen class uses a double buffer for
 * any rendering. It also includes a mouse and a keyboard listener.
 * 
 * @see Keyboard
 * @see Mouse
 */
final class Screen
        implements FocusListener
{
    /** Error message config. */
    private static final String MESSAGE_ERROR_CONFIG = "The configuration must exists !";
    /** Error message display. */
    private static final String MESSAGE_ERROR_DISPLAY = "No available display !";
    /** Error message applet. */
    private static final String MESSAGE_ERROR_APPLET = "Applet mode initialization failed !";
    /** Error message windowed. */
    private static final String MESSAGE_ERROR_WINDOWED = "Windowed mode initialization failed !";
    /** Error message unsupported fullscreen. */
    private static final String MESSAGE_ERROR_UNSUPPORTED_FULLSCREEN = "Unsupported fullscreen mode: ";
    /** Hidden cursor instance. */
    private static final Cursor CURSOR_HIDDEN = Screen.createHiddenCursor();
    /** Default cursor instance. */
    private static final Cursor CURSOR_DEFAULT = Cursor.getDefaultCursor();

    /**
     * Create instance for hidden cursor.
     * 
     * @return The hidden cursor.
     */
    private static Cursor createHiddenCursor()
    {
        try
        {
            final Toolkit toolkit = Toolkit.getDefaultToolkit();
            final Dimension dim = toolkit.getBestCursorSize(1, 1);
            final BufferedImage cursor = UtilityImage.createBufferedImage(dim.width, dim.height, Transparency.BITMASK);
            return toolkit.createCustomCursor(UtilityImage.applyMask(cursor, Color.BLACK), new Point(0, 0),
                    "hiddenCursor");
        }
        catch (final AWTError
                     | HeadlessException
                     | IndexOutOfBoundsException exception)
        {
            return Cursor.getDefaultCursor();
        }
    }

    /** Graphics device reference. */
    private final GraphicsDevice dev;
    /** Graphic configuration reference. */
    private final GraphicsConfiguration conf;
    /** Frame reference. */
    private final JFrame frame;
    /** Applet reference. */
    private final JApplet applet;
    /** Active graphic buffer reference. */
    private final Graphic graphics;
    /** Applet flag. */
    private final boolean hasApplet;
    /** Configuration reference. */
    private final Config config;
    /** Active sequence reference. */
    Sequence sequence;
    /** Buffer strategy reference. */
    private BufferStrategy buf;
    /** Image buffer reference. */
    private BufferedImage buffer;
    /** Graphic buffer reference. */
    private Graphics2D gbuf;
    /** Component listener for keyboard inputs. */
    private Component componentForKeyboard;
    /** Component listener for mouse inputs. */
    private Component componentForMouse;
    /** Component listener for cursor. */
    private Component componentForCursor;
    /** Windowed canvas. */
    private Canvas canvas;
    /** Fullscreen window. */
    private java.awt.Window window;

    /**
     * Create a new screen.
     * 
     * @param config The config reference.
     */
    Screen(Config config)
    {
        Check.notNull(config, Screen.MESSAGE_ERROR_CONFIG);
        if (GraphicsEnvironment.isHeadless())
        {
            throw new LionEngineException(Screen.MESSAGE_ERROR_DISPLAY);
        }

        // Initialize environment
        final GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        dev = env.getDefaultScreenDevice();
        conf = dev.getDefaultConfiguration();
        applet = config.getApplet();
        graphics = new Graphic();
        hasApplet = applet != null;
        this.config = config;

        // Prepare main frame
        frame = hasApplet ? null : initMainFrame();
        setResolution(config.getOutput());
    }

    /**
     * Set the screen config. Initialize the display.
     * 
     * @param output The output resolution
     */
    private void setResolution(Resolution output)
    {
        if (hasApplet)
        {
            initApplet(output);
        }
        else
        {
            if (config.isWindowed())
            {
                initWindowed(output);
            }
            else
            {
                initFullscreen(output, config.getDepth());
            }
        }
    }

    /**
     * Show the screen.
     */
    public void show()
    {
        frame.setVisible(true);
    }

    /**
     * Must be called when all rendering are done. It switch buffers before rendering.
     */
    public void update()
    {
        if (hasApplet)
        {
            applet.getGraphics().drawImage(buffer, 0, 0, null);
            graphics.setGraphics(gbuf);
        }
        else
        {
            buf.show();
            graphics.setGraphics((Graphics2D) buf.getDrawGraphics());
        }
    }

    /**
     * Close main frame. Dispose all graphics resources.
     */
    public void dispose()
    {
        graphics.clear(config.getOutput());
        update();
        if (hasApplet)
        {
            applet.destroy();
        }
        else
        {
            buf.dispose();
            frame.dispose();
        }
    }

    /**
     * Give focus to screen.
     */
    public void requestFocus()
    {
        if (hasApplet)
        {
            applet.requestFocus();
            applet.validate();
        }
        else
        {
            frame.requestFocus();
        }
        componentForMouse.requestFocus();
    }

    /**
     * Hide window mouse pointer.
     */
    public void hideCursor()
    {
        componentForCursor.setCursor(Screen.CURSOR_HIDDEN);
    }

    /**
     * Show window mouse pointer.
     */
    public void showCursor()
    {
        componentForCursor.setCursor(Screen.CURSOR_DEFAULT);
    }

    /**
     * Add a key listener.
     * 
     * @param listener The listener to add.
     */
    public void addKeyListener(KeyListener listener)
    {
        componentForKeyboard.addKeyListener(listener);
    }

    /**
     * Link keyboard to the screen (listening to).
     * 
     * @param keyboard The keyboard reference.
     */
    public void addKeyboard(Keyboard keyboard)
    {
        componentForKeyboard.addKeyListener((KeyListener) keyboard);
        componentForKeyboard.requestFocus();
        try
        {
            componentForKeyboard.setFocusTraversalKeysEnabled(false);
        }
        catch (final Exception exception)
        {
            Verbose.info("Transversal keys are not available !");
        }
    }

    /**
     * Link keyboard to the screen (listening to).
     * 
     * @param mouse The mouse reference.
     */
    public void addMouse(Mouse mouse)
    {
        componentForMouse.addMouseListener((MouseListener) mouse);
        componentForMouse.addMouseMotionListener((MouseMotionListener) mouse);
        componentForMouse.addMouseWheelListener((MouseWheelListener) mouse);
        componentForMouse.requestFocus();
    }

    /**
     * Set sequence reference.
     * 
     * @param sequence The sequence reference.
     */
    public void setSequence(Sequence sequence)
    {
        this.sequence = sequence;
    }

    /**
     * Set window icon from file.
     * 
     * @param filename The icon file name.
     */
    public void setIcon(String filename)
    {
        final ImageIcon icon = new ImageIcon(filename);
        frame.setIconImage(icon.getImage());
    }

    /**
     * Get current graphic.
     * 
     * @return The current graphic.
     */
    public Graphic getGraphic()
    {
        return graphics;
    }

    /**
     * Get the config.
     * 
     * @return The config.
     */
    public Config getConfig()
    {
        return config;
    }

    /**
     * Get frame container reference.
     * 
     * @return The frame reference.
     */
    public JFrame getFrame()
    {
        return frame;
    }

    /**
     * Get main frame location x.
     * 
     * @return The main frame location x.
     */
    public int getLocationX()
    {
        try
        {
            if (hasApplet)
            {
                return (int) applet.getLocationOnScreen().getX();
            }
            return (int) componentForMouse.getLocationOnScreen().getX();
        }
        catch (final IllegalComponentStateException exception)
        {
            return 0;
        }
    }

    /**
     * Get main frame location y.
     * 
     * @return The main frame location y.
     */
    public int getLocationY()
    {
        try
        {
            if (hasApplet)
            {
                return (int) applet.getLocationOnScreen().getY();
            }
            return (int) componentForMouse.getLocationOnScreen().getY();
        }
        catch (final IllegalComponentStateException exception)
        {
            return 0;
        }
    }

    /**
     * Add the mouse focus listener.
     */
    void prepareFocusListener()
    {
        try
        {
            componentForMouse.addFocusListener(this);
        }
        catch (final Exception exception)
        {
            Verbose.critical(Screen.class, "constructor", "Mouse focus listener can not be added !");
        }
    }

    /**
     * Start the main frame if has.
     */
    void start()
    {
        if (!hasApplet)
        {
            buf.show();
            graphics.setGraphics((Graphics2D) buf.getDrawGraphics());
            frame.validate();
            frame.setEnabled(true);
            frame.setVisible(true);
        }
    }

    /**
     * Prepare applet.
     * 
     * @param output The output resolution
     */
    private void initApplet(Resolution output)
    {
        try
        {
            buffer = UtilityImage.createBufferedImage(output.getWidth(), output.getHeight(), Transparency.OPAQUE);
            gbuf = buffer.createGraphics();
            graphics.setGraphics(gbuf);
            componentForKeyboard = applet;
            componentForMouse = applet;
            componentForCursor = applet;
            applet.validate();
        }
        catch (final Exception exception)
        {
            throw new LionEngineException(exception, Screen.MESSAGE_ERROR_APPLET);
        }
    }

    /**
     * Prepare windowed mode.
     * 
     * @param output The output resolution
     */
    private void initWindowed(Resolution output)
    {
        try
        {
            // Create canvas
            if (canvas == null)
            {
                canvas = new Canvas(conf);
                canvas.setBackground(Color.BLACK);
                canvas.setEnabled(true);
                canvas.setVisible(true);
                canvas.setIgnoreRepaint(true);

                // Add to main frame
                frame.add(canvas);
            }
            canvas.setPreferredSize(new Dimension(output.getWidth(), output.getHeight()));
            frame.pack();
            frame.setLocationRelativeTo(null);

            // Create buffer
            try
            {
                canvas.createBufferStrategy(2, conf.getBufferCapabilities());
            }
            catch (final AWTException exception)
            {
                canvas.createBufferStrategy(1);
            }
            buf = canvas.getBufferStrategy();

            // Set input listeners
            componentForKeyboard = canvas;
            componentForMouse = canvas;
            componentForCursor = frame;
            frame.validate();
        }
        catch (final Exception exception)
        {
            throw new LionEngineException(exception, Screen.MESSAGE_ERROR_WINDOWED);
        }
    }

    /**
     * Prepare fullscreen mode.
     * 
     * @param output The output resolution
     * @param depth The bit depth color.
     */
    private void initFullscreen(Resolution output, int depth)
    {
        try
        {
            // Create window
            if (window == null)
            {
                window = new java.awt.Window(frame, conf);
                window.setBackground(Color.BLACK);
                window.setIgnoreRepaint(true);

                // Set display
                frame.setUndecorated(true);
            }
            window.setPreferredSize(new Dimension(output.getWidth(), output.getHeight()));

            dev.setFullScreenWindow(window);
            final DisplayMode disp = new DisplayMode(output.getWidth(), output.getHeight(), depth, output.getRate());
            dev.setDisplayMode(disp);
            window.validate();

            // Create buffer
            try
            {
                window.createBufferStrategy(2, conf.getBufferCapabilities());
            }
            catch (final AWTException exception)
            {
                window.createBufferStrategy(1);
            }
            buf = window.getBufferStrategy();

            // Set input listeners
            componentForKeyboard = frame;
            componentForMouse = window;
            componentForCursor = window;
            frame.validate();
        }
        catch (final UnsupportedOperationException
                     | IllegalComponentStateException
                     | IllegalArgumentException exception)
        {
            final StringBuilder builder = new StringBuilder("Supported display mode:\n");
            int i = 0;
            for (final DisplayMode display : dev.getDisplayModes())
            {
                final StringBuilder widthSpace = new StringBuilder("");
                final int width = display.getWidth();
                if (width < 1000)
                {
                    widthSpace.append(" ");
                }
                final StringBuilder heightSpace = new StringBuilder("");
                final int height = display.getHeight();
                if (height < 1000)
                {
                    heightSpace.append(" ");
                }
                final StringBuilder freqSpace = new StringBuilder("");
                final int freq = display.getRefreshRate();
                if (freq < 100)
                {
                    freqSpace.append(" ");
                }
                builder.append("[").append(widthSpace).append(width).append("*").append(heightSpace).append(height)
                        .append("*").append(display.getBitDepth()).append(" @").append(freqSpace).append(freq)
                        .append("Hz] ");
                i++;
                if (i % 5 == 0)
                {
                    builder.append("\n");
                }
            }
            throw new LionEngineException(Screen.MESSAGE_ERROR_UNSUPPORTED_FULLSCREEN,
                    String.valueOf(output.getWidth()), "*", String.valueOf(output.getHeight()), "*",
                    String.valueOf(depth), " @", String.valueOf(output.getRate()), "Hz", "\n", builder.toString());
        }
    }

    /**
     * Initialize the main frame.
     * 
     * @return The created main frame.
     */
    private JFrame initMainFrame()
    {
        final JFrame frame = new JFrame(Engine.getProgramName(), conf);

        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent event)
            {
                sequence.end();
            }
        });
        frame.setResizable(false);
        frame.setIgnoreRepaint(true);

        return frame;
    }

    /*
     * FocusListener
     */

    @Override
    public void focusGained(FocusEvent event)
    {
        if (sequence != null)
        {
            sequence.onFocusGained();
        }
    }

    @Override
    public void focusLost(FocusEvent event)
    {
        if (sequence != null)
        {
            sequence.onLostFocus();
        }
    }
}
