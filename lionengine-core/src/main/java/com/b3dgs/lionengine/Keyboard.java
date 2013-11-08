/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine;

/**
 * Represents the keyboard input. Gives informations such as pressed key and code.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public interface Keyboard
{
    /**
     * Check if the key is currently pressed.
     * 
     * @param key The key to check.
     * @return <code>true</code> if pressed, <code>false</code> else.
     */
    boolean isPressed(Integer key);

    /**
     * Check if the key is currently pressed (not continuously).
     * 
     * @param key The key to check.
     * @return <code>true</code> if pressed, <code>false</code> else.
     */
    boolean isPressedOnce(Integer key);

    /**
     * Get the current pressed key code.
     * 
     * @return The pressed key code.
     */
    Integer getKeyCode();

    /**
     * Get the current pressed key name.
     * 
     * @return The pressed key name.
     */
    char getKeyName();
    
    /**
     * Get the text key of the key code.
     * 
     * @param code The key code.
     * @return The key text.
     */
    String getKeyText(int code);

    /**
     * Check if the keyboard is currently used (at least one pressed key).
     * 
     * @return <code>true</code> if has at least on pressed key, <code>false</code> else (no pressed key).
     */
    boolean used();
}
