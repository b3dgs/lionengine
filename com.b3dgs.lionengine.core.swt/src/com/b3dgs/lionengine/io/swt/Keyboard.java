/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.b3dgs.lionengine.io.swt;

import org.eclipse.swt.SWT;

import com.b3dgs.lionengine.io.InputDeviceDirectional;

/**
 * Keyboard input.
 */
public interface Keyboard extends InputDeviceDirectional
{
    /** Arrow up key. */
    Integer UP = Integer.valueOf(SWT.ARROW_UP);
    /** Arrow down key. */
    Integer DOWN = Integer.valueOf(SWT.ARROW_DOWN);
    /** Arrow right key. */
    Integer RIGHT = Integer.valueOf(SWT.ARROW_RIGHT);
    /** Arrow left key. */
    Integer LEFT = Integer.valueOf(SWT.ARROW_LEFT);
    /** CTRL key. */
    Integer CONTROL = Integer.valueOf(SWT.CONTROL);
    /** ALT key. */
    Integer ALT = Integer.valueOf(SWT.ALT);
    /** Escape key. */
    Integer ESCAPE = Integer.valueOf(SWT.ESC);
    /** Enter key. */
    Integer ENTER = Integer.valueOf(SWT.LF);
    /** Back Space key. */
    Integer BACK_SPACE = Integer.valueOf(SWT.BS);
    /** Tab key. */
    Integer TAB = Integer.valueOf(SWT.TAB);
    /** No key code value. */
    Integer NO_KEY_CODE = Integer.valueOf(-1);

    /**
     * Add an action that will be triggered on pressed state.
     * <p>
     * Alternative usage with classic programming style can be achieved with {@link #isPressed(Integer)} or
     * {@link #isPressedOnce(Integer)}.
     * </p>
     * 
     * @param key The action key.
     * @param action The action reference.
     */
    void addActionPressed(Integer key, EventAction action);

    /**
     * Add an action that will be triggered on released state.
     * <p>
     * Alternative usage with classic programming style can be achieved with {@link #isPressed(Integer)} or
     * {@link #isPressedOnce(Integer)}.
     * </p>
     * 
     * @param key The action key.
     * @param action The action reference.
     */
    void addActionReleased(Integer key, EventAction action);

    /**
     * Remove all pressed actions.
     */
    void removeActionsPressed();

    /**
     * Remove all released actions.
     */
    void removeActionsReleased();

    /**
     * Check if the key is currently pressed.
     * <p>
     * Alternative usage with event programming style can be achieved with
     * {@link #addActionPressed(Integer, EventAction)} and {@link #addActionReleased(Integer, EventAction)}.
     * </p>
     * 
     * @param key The key to check.
     * @return <code>true</code> if pressed, <code>false</code> else.
     */
    boolean isPressed(Integer key);

    /**
     * Check if the key is currently pressed (not continuously).
     * <p>
     * Alternative usage with event programming style can be achieved with
     * {@link #addActionPressed(Integer, EventAction)} and {@link #addActionReleased(Integer, EventAction)}.
     * </p>
     * 
     * @param key The key to check.
     * @return <code>true</code> if pressed, <code>false</code> else.
     */
    boolean isPressedOnce(Integer key);

    /**
     * Get the current pressed key code.
     * 
     * @return The pressed key code (<code>{@value #NO_KEY_CODE}</code> if key never pressed).
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
