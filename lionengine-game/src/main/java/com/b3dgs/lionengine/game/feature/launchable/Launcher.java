/*
 * Copyright (C) 2013-2021 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.launchable;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Listenable;
import com.b3dgs.lionengine.Localizable;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.game.Direction;
import com.b3dgs.lionengine.game.Feature;
import com.b3dgs.lionengine.game.feature.FeatureInterface;

/**
 * Represents something that can throw a {@link Launchable} at a defined rate from a defined location using a specified
 * vector.
 * 
 * @see Launchable
 */
@FeatureInterface
public interface Launcher extends Feature, Updatable, Listenable<LauncherListener>
{
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
     * @return <code>true</code> if fired, <code>false</code> else.
     * @throws LionEngineException If the fired object is not a {@link Launchable}.
     */
    boolean fire();

    /**
     * Fire with the specified configuration. {@link LauncherListener} will be notified with
     * {@link LauncherListener#notifyFired()} first, and {@link LaunchableListener#notifyFired(Launchable)} for each
     * launch.
     * 
     * @param initial The fire initial speed, used to transfer the initial force on launch.
     * @return <code>true</code> if fired, <code>false</code> else.
     * @throws LionEngineException If the fired object is not a {@link Launchable}.
     */
    boolean fire(Direction initial);

    /**
     * Fire with the specified configuration. {@link LauncherListener} will be notified with
     * {@link LauncherListener#notifyFired()} first, and {@link LaunchableListener#notifyFired(Launchable)} for each
     * launch.
     * 
     * @param target The launch target.
     * @return <code>true</code> if fired, <code>false</code> else.
     * @throws LionEngineException If the fired object is not a {@link Launchable}.
     */
    boolean fire(Localizable target);

    /**
     * Fire with the specified configuration. {@link LauncherListener} will be notified with
     * {@link LauncherListener#notifyFired()} first, and {@link LaunchableListener#notifyFired(Launchable)} for each
     * launch.
     * 
     * @param initial The fire initial speed, used to transfer the initial force on launch.
     * @param target The launch target.
     * @return <code>true</code> if fired, <code>false</code> else.
     * @throws LionEngineException If the fired object is not a {@link Launchable}.
     */
    boolean fire(Direction initial, Localizable target);

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

    /**
     * Get the current level.
     * 
     * @return The current level.
     */
    int getLevel();

    /**
     * Get the fire rate.
     * 
     * @return The fire rate in milliseconds.
     */
    long getRate();
}
