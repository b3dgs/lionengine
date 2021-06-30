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
    /** Smooth speed value. */
    private static final int SMOOTH_SPEED = 2;

    /** Camera service reference. */
    private final Camera camera;
    /** Followed element (can be <code>null</code>). */
    private Localizable tracked;
    /** Horizontal offset. */
    private int h;
    /** Vertical offset. */
    private int v;
    /** Smooth horizontal flag. */
    private boolean smoothX;
    /** Smooth vertical flag. */
    private boolean smoothY;

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

        camera = services.get(Camera.class);

        addFeature(new RefreshableModel(extrp ->
        {
            if (tracked != null)
            {
                final double dx = tracked.getX() - camera.getWidth() / 2.0 + h;
                final double dy = tracked.getY() - camera.getHeight() / 2.0 + v;

                final int sideX = updateSmoothX(dx);
                final int sideY = updateSmoothY(dy);

                camera.moveLocation(extrp, sideX, sideY);

                if (smoothX && !smoothY)
                {
                    camera.setLocationY(dy);
                }
                else if (!smoothX && smoothY)
                {
                    camera.setLocationX(dx);
                }
                else if (!smoothX && !smoothY)
                {
                    camera.setLocation(dx, dy);
                }
            }
        }));
    }

    /**
     * Update horizontal smooth.
     * 
     * @param dx The destination.
     * @return The side.
     */
    private int updateSmoothX(double dx)
    {
        int sideX = 0;
        if (smoothX)
        {
            if (camera.getX() < dx - SMOOTH_SPEED)
            {
                sideX = SMOOTH_SPEED;
            }
            else if (camera.getX() > dx + SMOOTH_SPEED)
            {
                sideX = -SMOOTH_SPEED;
            }
            else
            {
                sideX = 0;
                smoothX = false;
            }
        }
        return sideX;
    }

    /**
     * Update vertical smooth.
     * 
     * @param dy The destination.
     * @return The side.
     */
    private int updateSmoothY(double dy)
    {
        int sideY = 0;
        if (smoothY)
        {
            if (camera.getY() < dy - SMOOTH_SPEED)
            {
                sideY = SMOOTH_SPEED;
            }
            else if (camera.getY() > dy + SMOOTH_SPEED)
            {
                sideY = -SMOOTH_SPEED;
            }
            else
            {
                sideY = 0;
                smoothY = false;
            }
        }
        return sideY;
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
     * @param localizable The localizable to track.
     */
    public void track(Localizable localizable)
    {
        track(localizable, false);
    }

    /**
     * Track the specified localizable.
     * 
     * @param localizable The localizable to track.
     * @param smooth <code>true</code> to enable smooth, <code>false</code> else.
     */
    public void track(Localizable localizable, boolean smooth)
    {
        tracked = localizable;
        smoothX = smooth;
        smoothY = smooth;

        if (!smooth && tracked != null)
        {
            camera.teleport(tracked.getX() - camera.getWidth() / 2.0 + h,
                            tracked.getY() - camera.getHeight() / 2.0 + v);
        }
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
     * @param featurable The featurable to follow.
     * @throws LionEngineException If missing feature.
     */
    public void track(Featurable featurable)
    {
        final Transformable transformable = featurable.getFeature(Transformable.class);
        track(transformable);
    }

    /**
     * Stop tracking.
     */
    public void stop()
    {
        tracked = null;
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
