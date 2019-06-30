/*
 * Copyright (C) 2013-2019 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.Feature;

/**
 * Represents something identified by a unique number.
 * <p>
 * Can request to be removed in order to be reused.
 * </p>
 */
@FeatureInterface
public interface Identifiable extends Feature
{
    /**
     * Add a listener.
     * 
     * @param listener The listener reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    void addListener(IdentifiableListener listener);

    /**
     * Remove a listener.
     * 
     * @param listener The listener reference.
     * @throws LionEngineException If invalid argument.
     */
    void removeListener(IdentifiableListener listener);

    /**
     * Get the Id (<code>null</code> will be returned once removed after a call to {@link #destroy()}).
     * 
     * @return The unique Id.
     */
    Integer getId();

    /**
     * Declare as removable. Can be destroyed only one time.
     * {@link #notifyDestroyed()} should be called when recycle can be performed.
     */
    void destroy();

    /**
     * Notify effective destruction, and allow to recycle destroyed Id.
     */
    void notifyDestroyed();
}
