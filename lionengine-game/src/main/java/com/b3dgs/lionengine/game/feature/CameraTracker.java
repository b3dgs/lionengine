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

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Localizable;

/**
 * Camera tracking implementation.
 */
public class CameraTracker extends FeaturableModel
{
    /** Followed element (can be <code>null</code>). */
    private Localizable tracked;

    /**
     * Create tracker.
     * <p>
     * The {@link Services} must provide:
     * </p>
     * <ul>
     * <li>{@link Camera}</li>
     * </ul>
     * 
     * @param services The services reference.
     */
    public CameraTracker(Services services)
    {
        super();

        final Camera camera = services.get(Camera.class);

        addFeature(new RefreshableModel(extrp ->
        {
            if (tracked != null)
            {
                camera.setLocation(tracked.getX() - camera.getWidth() / 2.0, tracked.getY() - camera.getHeight() / 2.0);
            }
        }));
    }

    /**
     * Track the specified localizable.
     * 
     * @param localizable The localizable to track.
     */
    public void track(Localizable localizable)
    {
        tracked = localizable;
    }

    /**
     * Track the specified featurable.
     * <p>
     * The {@link Featurable} must have:
     * </p>
     * <ul>
     * <li>{@link Transformable}</li>
     * </ul>
     * 
     * @param featurable The featurable to follow, <code>null</code> to stop follow.
     * @throws LionEngineException If missing feature.
     */
    public void track(Featurable featurable)
    {
        tracked = featurable.getFeature(Transformable.class);
    }
}
