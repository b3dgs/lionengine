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

import com.b3dgs.lionengine.core.Config;

/**
 * Represents the mouse input. Gives informations such as mouse click and cursor location.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public interface Mouse
{
    /**
     * Update the mouse.
     */
    void update();

    /**
     * Lock mouse at its center.
     */
    void lock();

    /**
     * Lock mouse at specified location.
     * 
     * @param x The location x.
     * @param y The location y.
     */
    void lock(int x, int y);

    /**
     * Perform a click.
     * 
     * @param click The click to perform.
     */
    void doClick(int click);

    /**
     * Perform a click at specified coordinate.
     * 
     * @param click The click to perform.
     * @param x The location x.
     * @param y The location y.
     */
    void doClickAt(int click, int x, int y);

    /**
     * Set the config.
     * 
     * @param config The config.
     */
    void setConfig(Config config);

    /**
     * Set mouse center for lock operation.
     * 
     * @param x The location x.
     * @param y The location y.
     */
    void setCenter(int x, int y);

    /**
     * Get current pressed click.
     * 
     * @return The pressed click.
     */
    int getMouseClick();

    /**
     * Get location on screen x.
     * 
     * @return The location on screen x.
     */
    int getOnScreenX();

    /**
     * Get location on screen y.
     * 
     * @return The location on screen y.
     */
    int getOnScreenY();

    /**
     * Get location on window x.
     * 
     * @return The location on window x.
     */
    int getOnWindowX();

    /**
     * Get location on window y.
     * 
     * @return The location on window y.
     */
    int getOnWindowY();

    /**
     * Get mouse horizontal move.
     * 
     * @return The horizontal move.
     */
    int getMoveX();

    /**
     * Get mouse vertical move.
     * 
     * @return The vertical move.
     */
    int getMoveY();

    /**
     * Check if click is pressed.
     * 
     * @param click The click to check.
     * @return The pressed state.
     */
    boolean hasClicked(int click);

    /**
     * Check if click is pressed once only (ignore 'still clicked').
     * 
     * @param click The click to check.
     * @return The pressed state.
     */
    boolean hasClickedOnce(int click);

    /**
     * Check if mouse moved.
     * 
     * @return <code>true</code> if moved, <code>false</code> else.
     */
    boolean moved();
}
