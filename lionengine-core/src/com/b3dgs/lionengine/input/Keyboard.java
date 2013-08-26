package com.b3dgs.lionengine.input;

import java.awt.event.KeyEvent;

/**
 * Represents the keyboard input. Gives informations such as pressed key and code.
 */
public interface Keyboard
{
    /** Arrow up key. */
    Integer UP = Integer.valueOf(KeyEvent.VK_UP);
    /** Arrow down key. */
    Integer DOWN = Integer.valueOf(KeyEvent.VK_DOWN);
    /** Arrow right key. */
    Integer RIGHT = Integer.valueOf(KeyEvent.VK_RIGHT);
    /** Arrow left key. */
    Integer LEFT = Integer.valueOf(KeyEvent.VK_LEFT);
    /** CTRL key. */
    Integer CONTROL = Integer.valueOf(KeyEvent.VK_CONTROL);
    /** ALT key. */
    Integer ALT = Integer.valueOf(KeyEvent.VK_ALT);
    /** Escape key. */
    Integer ESCAPE = Integer.valueOf(KeyEvent.VK_ESCAPE);
    /** Space key. */
    Integer SPACE = Integer.valueOf(KeyEvent.VK_SPACE);
    /** Tab key. */
    Integer TAB = Integer.valueOf(KeyEvent.VK_TAB);
    /** Enter key. */
    Integer ENTER = Integer.valueOf(KeyEvent.VK_ENTER);

    /**
     * Check if the key is currently pressed.
     * 
     * @param key The key to check.
     * @return <code>true</code> if pressed, <code>false</code> else.
     * @see KeyEvent
     */
    boolean isPressed(Integer key);

    /**
     * Check if the key is currently pressed (not continuously).
     * 
     * @param key The key to check.
     * @return <code>true</code> if pressed, <code>false</code> else.
     * @see KeyEvent
     */
    boolean isPressedOnce(Integer key);

    /**
     * Get the current pressed key code.
     * 
     * @return The pressed key code.
     * @see KeyEvent
     */
    Integer getKeyCode();

    /**
     * Get the current pressed key name.
     * 
     * @return The pressed key name.
     */
    char getKeyName();

    /**
     * Check if the keyboard is currently used (at least one pressed key).
     * 
     * @return <code>true</code> if has at least on pressed key, <code>false</code> else (no pressed key).
     */
    boolean used();
}
