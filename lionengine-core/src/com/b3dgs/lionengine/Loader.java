package com.b3dgs.lionengine;

import com.b3dgs.lionengine.input.Input;
import com.b3dgs.lionengine.input.Keyboard;
import com.b3dgs.lionengine.input.Mouse;

/**
 * Engine starter, need to be called for sequence selection and screen initializer. It already handles a screen using a
 * specified configuration. Any implemented sequence has to be added into the loader. It just needs a single instance, a
 * first sequence reference, and a call to start(), in the main.
 */
public final class Loader
{
    /** Error message config. */
    private static final String MESSAGE_ERROR_CONFIG = "Configuration must not be null !";
    /** Error message already started. */
    private static final String MESSAGE_ERROR_STARTED = "Loader has already been started !";
    /** Error message sequence interrupted. */
    private static final String MESSAGE_ERROR_SEQUENCE = "Sequence badly interrupted !";
    /** Screen reference. */
    private final Screen screen;
    /** Keyboard reference. */
    private final Keyboard keyboard;
    /** Mouse reference. */
    private final Mouse mouse;
    /** Thread loader. */
    private final LoaderThread thread;
    /** Next sequence pointer. */
    private Sequence nextSequence;
    /** Started flag. */
    private boolean started;

    /**
     * Create a loader.
     * 
     * @param config The configuration used (must not be null).
     */
    public Loader(Config config)
    {
        Check.notNull(config, Loader.MESSAGE_ERROR_CONFIG);
        screen = new Screen(config);
        screen.prepareFocusListener();
        keyboard = Input.createKeyboard();
        mouse = Input.createMouse(config.external.getWidth() / (double) config.internal.getWidth(),
                config.external.getHeight() / (double) config.internal.getHeight());
        screen.addKeyboard(keyboard);
        screen.addMouse(mouse);
        thread = new LoaderThread(this);
        nextSequence = null;
    }

    /**
     * Start the loader. Has to be called only one time.
     * 
     * @param sequence The the next sequence to start.
     */
    public void start(Sequence sequence)
    {
        if (!started)
        {
            nextSequence = sequence;
            screen.start();
            started = true;
            thread.start();
        }
        else
        {
            throw new LionEngineException(Loader.MESSAGE_ERROR_STARTED);
        }
    }

    /**
     * Run loader.
     */
    void run()
    {
        while (nextSequence != null)
        {
            // Select next sequence
            final Sequence sequence = nextSequence;
            sequence.setTitle(sequence.getClass().getName());
            screen.setSequence(sequence);
            Verbose.info("Starting sequence ", sequence.getClass().getName());
            sequence.start();

            // Wait for sequence end
            try
            {
                sequence.join();
            }
            catch (final InterruptedException exception)
            {
                throw new LionEngineException(exception, Loader.MESSAGE_ERROR_SEQUENCE);
            }
            Verbose.info("Sequence ", sequence.getClass().getName(), " terminated");
            nextSequence = sequence.getNextSequence();

        }
        screen.dispose();
        Verbose.info("LionEngine terminated");
    }

    /**
     * Get the screen.
     * 
     * @return The screen.
     */
    Screen getScreen()
    {
        return screen;
    }

    /**
     * Get the keyboard.
     * 
     * @return The keyboard.
     */
    Keyboard getKeyboard()
    {
        return keyboard;
    }

    /**
     * Get the mouse.
     * 
     * @return The mouse.
     */
    Mouse getMouse()
    {
        return mouse;
    }
}
