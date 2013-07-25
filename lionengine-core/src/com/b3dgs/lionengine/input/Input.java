package com.b3dgs.lionengine.input;

/**
 * Handle game inputs, mouse and keyboard.
 */
public final class Input
{
    /**
     * Private constructor.
     */
    private Input()
    {
        throw new RuntimeException();
    }

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
     * @param xRatio The horizontal screen ratio.
     * @param yRatio The vertical screen ratio.
     * @return The created mouse listener.
     */
    public static Mouse createMouse(double xRatio, double yRatio)
    {
        return new MouseImpl(xRatio, yRatio);
    }
}
