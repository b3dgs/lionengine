package com.b3dgs.lionengine.input;

/**
 * Handle game inputs, mouse and keyboard.
 */
public final class Input
{
    /**
     * Create a keyboard input.
     * 
     * @return The created keyboard listener.
     */
    public static Keyboard createKeyboard()
    {
        return new KeyboardImpl();
    }

    /**
     * Create a mouse input.
     * 
     * @return The created mouse listener.
     */
    public static Mouse createMouse()
    {
        return new MouseImpl();
    }

    /**
     * Private constructor.
     */
    private Input()
    {
        throw new RuntimeException();
    }
}
