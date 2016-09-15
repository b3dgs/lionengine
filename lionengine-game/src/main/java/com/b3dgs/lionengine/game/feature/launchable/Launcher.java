/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.launchable;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Localizable;
import com.b3dgs.lionengine.game.feature.refreshable.Refreshable;

/**
 * Represents something that can throw a {@link Launchable} at a defined rate from a defined location using a specified
 * vector.
 * 
 * @see Launchable
 */
public interface Launcher extends Refreshable
{
    /**
     * Add a launcher listener.
     * 
     * @param listener The launcher listener to add.
     */
    void addListener(LauncherListener listener);

    /**
     * Add a launchable listener.
     * 
     * @param listener The launchable listener to add.
     */
    void addListener(LaunchableListener listener);

    /**
     * Fire with the specified configuration. {@link LauncherListener} will be notified with
     * {@link LauncherListener#notifyFired()} first, and {@link LaunchableListener#notifyFired(Launchable)} for each
     * launch.
     * 
     * @throws LionEngineException If the fired object is not a {@link Launchable}.
     */
    void fire();

    /**
     * Fire with the specified configuration. {@link LauncherListener} will be notified with
     * {@link LauncherListener#notifyFired()} first, and {@link LaunchableListener#notifyFired(Launchable)} for each
     * launch.
     * 
     * @param target The launch target.
     * @throws LionEngineException If the fired object is not a {@link Launchable}.
     */
    void fire(Localizable target);

    /**
     * Set the launcher position offset. Modify the {@link Launchable} starting position on fire.
     * 
     * @param x The horizontal offset.
     * @param y The vertical offset.
     */
    void setOffset(int x, int y);

    /**
     * Set the level used.
     * 
     * @param level The level used (must be superior or equal to 0).
     * @throws LionEngineException If wrong argument.
     */
    void setLevel(int level);

    /**
     * Set the fire rate.
     * 
     * @param rate The fire rate in millisecond.
     */
    void setRate(long rate);

    /**
     * Get the horizontal offset.
     * 
     * @return The horizontal offset.
     */
    int getOffsetX();

    /**
     * Get the vertical offset.
     * 
     * @return The vertical offset.
     */
    int getOffsetY();
}
