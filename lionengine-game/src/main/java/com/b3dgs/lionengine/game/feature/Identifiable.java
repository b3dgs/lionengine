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
package com.b3dgs.lionengine.game.feature;

import com.b3dgs.lionengine.game.Feature;

/**
 * Represents something which can be identified by a unique ID.
 * Can request to be removed and free its ID in order to be reused.
 */
@FeatureInterface
public interface Identifiable extends Feature
{
    /**
     * Add an identifiable listener.
     * 
     * @param listener The listener reference.
     */
    void addListener(IdentifiableListener listener);

    /**
     * Remove an identifiable listener.
     * 
     * @param listener The listener reference.
     */
    void removeListener(IdentifiableListener listener);

    /**
     * Get the ID (<code>null</code> will be returned once removed after a call to {@link #destroy()}).
     * 
     * @return The object unique ID.
     */
    Integer getId();

    /**
     * Declare as removable. Can be destroyed only one time.
     * {@link #notifyDestroyed()} should be called when recycle can be performed.
     */
    void destroy();

    /**
     * Notify effective destruction, and allow to recycle destroyed ID. Any call to {@link #getId()} will return
     * <code>null</code>.
     * Called by the identifiable handler after a call to {@link #destroy()}.
     */
    void notifyDestroyed();
}
