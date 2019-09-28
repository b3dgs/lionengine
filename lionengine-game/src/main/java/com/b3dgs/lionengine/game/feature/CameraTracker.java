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

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Localizable;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.game.Feature;

/**
 * Camera tracking implementation.
 */
public class CameraTracker extends FeaturableAbstract
{
    /** Followed element (can be <code>null</code>). */
    private Localizable tracked;
    /** Horizontal offset. */
    private int h;
    /** Vertical offset. */
    private int v;

    /**
     * Create tracker.
     * <p>
     * The {@link Services} must provide:
     * </p>
     * <ul>
     * <li>{@link Camera}</li>
     * </ul>
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    public CameraTracker(Services services)
    {
        super();

        Check.notNull(services);

        final Camera camera = services.get(Camera.class);

        addFeature(new RefreshableModel(extrp ->
        {
            if (tracked != null)
            {
                camera.setLocation(Math.floor(tracked.getX()) - camera.getWidth() / 2.0 + h,
                                   Math.ceil(tracked.getY()) - camera.getHeight() / 2.0 + v);
            }
        }));
    }

    /**
     * Set tracking offset.
     * 
     * @param h The horizontal offset.
     * @param v The vertical offset.
     */
    public void setOffset(int h, int v)
    {
        this.h = h;
        this.v = v;
    }

    /**
     * Track the specified localizable.
     * 
     * @param localizable The localizable to track, <code>null</code> to stop follow.
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

    /*
     * Featurable
     */

    @Override
    public final void addFeature(Feature feature)
    {
        super.addFeature(feature);
    }

    /**
     * Return always <code>null</code>.
     */
    @Override
    public Media getMedia()
    {
        return null;
    }
}
