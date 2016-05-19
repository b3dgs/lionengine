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
package com.b3dgs.lionengine.game.camera;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Localizable;
import com.b3dgs.lionengine.game.handler.FeatureModel;
import com.b3dgs.lionengine.game.handler.Handlable;
import com.b3dgs.lionengine.game.handler.Refreshable;
import com.b3dgs.lionengine.game.handler.Services;
import com.b3dgs.lionengine.game.object.feature.transformable.Transformable;

/**
 * Camera tracking feature implementation.
 */
public class CameraTracker extends FeatureModel implements Refreshable
{
    /** Tracked localizable. */
    private Localizable tracked;
    private Camera camera;

    /**
     * Create tracker.
     */
    public CameraTracker()
    {
        super();
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
     * Track the specified handlable.
     * <p>
     * The {@link Handlable} must provide the following features:
     * </p>
     * <ul>
     * <li>{@link Transformable}</li>
     * </ul>
     * 
     * @param handlable The handlable to follow, <code>null</code> to stop follow.
     * @throws LionEngineException If missing feature.
     */
    public void track(Handlable handlable)
    {
        tracked = handlable.getFeature(Transformable.class);
    }

    /*
     * Refreshable
     */

    @Override
    public void prepare(Handlable owner, Services services)
    {
        super.prepare(owner, services);

        camera = services.get(Camera.class);
    }

    @Override
    public void update(double extrp)
    {
        if (tracked != null)
        {
            camera.setLocation(tracked.getX(), tracked.getY());
        }
    }
}
