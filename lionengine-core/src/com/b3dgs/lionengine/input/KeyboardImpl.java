package com.b3dgs.lionengine.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Set;

/**
 * Keyboard input implementation.
 */
class KeyboardImpl
        implements Keyboard, KeyListener
{
    /** List of keys. */
    private final Set<Integer> keys;
    /** Pressed states. */
    private final Set<Integer> pressed;
    /** Last key code. */
    private Integer lastCode;
    /** Last key name. */
    private char lastKeyName;

    /**
     * Create a keyboard input.
     */
    KeyboardImpl()
    {
        keys = new HashSet<>();
        pressed = new HashSet<>();
        lastKeyName = ' ';
        lastCode = Integer.valueOf(-1);
    }

    /*
     * Keyboard
     */

    @Override
    public boolean isPressed(Integer key)
    {
        return keys.contains(key);
    }

    @Override
    public boolean isPressedOnce(Integer key)
    {
        if (keys.contains(key))
        {
            if (!pressed.contains(key))
            {
                pressed.add(key);
                return true;
            }
        }
        return false;
    }

    @Override
    public Integer getKeyCode()
    {
        return lastCode;
    }

    @Override
    public char getKeyName()
    {
        return lastKeyName;
    }

    @Override
    public boolean used()
    {
        return !keys.isEmpty();
    }

    /*
     * KeyListener
     */

    @Override
    public void keyPressed(KeyEvent event)
    {
        lastCode = Integer.valueOf(event.getKeyCode());
        lastKeyName = event.getKeyChar();
        keys.add(Integer.valueOf(event.getKeyCode()));
    }

    @Override
    public void keyReleased(KeyEvent event)
    {
        lastCode = Integer.valueOf(-1);
        lastKeyName = ' ';
        keys.remove(Integer.valueOf(event.getKeyCode()));
        pressed.remove(Integer.valueOf(event.getKeyCode()));
    }

    @Override
    public void keyTyped(KeyEvent event)
    {
        // Nothing to do
    }
}
