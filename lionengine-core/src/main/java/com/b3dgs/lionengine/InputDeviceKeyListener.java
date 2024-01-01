/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.lionengine;

/**
 * Input device with key listener.
 */
public interface InputDeviceKeyListener
{
    /**
     * Called when a key is pressed.
     * 
     * @param keyCode The key code.
     * @param keyChar The key character.
     */
    void keyPressed(int keyCode, char keyChar);

    /**
     * Called when a key is released.
     * 
     * @param keyCode The key code.
     * @param keyChar The key character.
     */
    void keyReleased(int keyCode, char keyChar);
}
