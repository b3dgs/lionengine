package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity;

import com.b3dgs.lionengine.input.Keyboard;

/**
 * List of entity actions.
 */
public enum EntityAction
{
    /** Move left. */
    MOVE_LEFT(Keyboard.LEFT),
    /** Move right. */
    MOVE_RIGHT(Keyboard.RIGHT),
    /** Move down. */
    MOVE_DOWN(Keyboard.DOWN),
    /** Jump. */
    JUMP(Keyboard.UP),
    /** Attack. */
    ATTACK(Keyboard.CONTROL);

    /** Values. */
    public static final EntityAction[] VALUES = EntityAction.values();
    /** The key binding (used in case of control with keyboard). */
    private final Integer key;

    /**
     * Constructor.
     * 
     * @param key The key binding.
     */
    private EntityAction(Integer key)
    {
        this.key = key;
    }

    /**
     * Get the key binding.
     * 
     * @return The key binding.
     */
    public Integer getKey()
    {
        return key;
    }
}
