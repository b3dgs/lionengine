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
package com.b3dgs.lionengine.editor.world.updater;

import com.b3dgs.lionengine.game.Camera;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.object.Services;
import com.b3dgs.lionengine.util.UtilMath;

/**
 * Handle the world zoom capability.
 */
public class WorldZoomUpdater implements WorldMouseClickListener, WorldMouseScrollListener
{
    /** Default zoom value. */
    public static final int ZOOM_DEFAULT = 100;
    /** Maximum zoom value. */
    public static final int ZOOM_MAX = 900;
    /** Minimum zoom value. */
    public static final int ZOOM_MIN = 25;

    /** Camera reference. */
    private final Camera camera;
    /** Map reference. */
    private final MapTile map;
    /** Zoom level in percent. */
    private int zoomPercent;
    /** Old scale. */
    private double oldScale;
    /** Zoom enabled flag. */
    private boolean enabled;

    /**
     * Create the world zoom.
     * 
     * @param services The services reference.
     */
    public WorldZoomUpdater(Services services)
    {
        camera = services.get(Camera.class);
        map = services.get(MapTile.class);
        zoomPercent = ZOOM_DEFAULT;
        enabled = true;
    }

    /**
     * Perform a single zoom in.
     */
    public void zoomIn()
    {
        zoom(1);
    }

    /**
     * Perform a single zoom out.
     */
    public void zoomOut()
    {
        zoom(-1);
    }

    /**
     * Perform a zoom.
     * 
     * @param step The number of zoom.
     */
    public void zoom(int step)
    {
        final int tw = map.getTileWidth();
        final double scale = getScale();
        final double next;
        if (zoomPercent < 100)
        {
            next = step;
        }
        else
        {
            next = step * scale;
        }
        final int percent = (int) Math.ceil((tw * scale + next) / tw * 100.0);
        setPercent(percent);
    }

    /**
     * Set the zoom percent value.
     * 
     * @param percent The new zoom percent value.
     */
    public void setPercent(int percent)
    {
        zoomPercent = UtilMath.clamp(percent, ZOOM_MIN, ZOOM_MAX);
    }

    /**
     * Get the zoom percent value.
     * 
     * @return The zoom percent value.
     */
    public int getPercent()
    {
        return zoomPercent;
    }

    /**
     * Get the zoom scale.
     * 
     * @return The zoom scale.
     */
    public double getScale()
    {
        return zoomPercent / 100.0;
    }

    /**
     * Lock scroll to cursor.
     * 
     * @param mx The current horizontal location.
     * @param my The current vertical location.
     */
    private void updateScrollToCursor(int mx, int my)
    {
        final double ox;
        final double oy;
        final double scale = getScale();
        final double scaleDiff = scale - oldScale;

        ox = mx / scale * scaleDiff;
        oy = (camera.getHeight() - my) / scale * scaleDiff;
        camera.teleport(camera.getX() + ox, camera.getY() + oy);
    }

    /*
     * WorldMouseClickListener
     */

    @Override
    public void onMousePressed(int click, int mx, int my)
    {
        enabled = false;
    }

    @Override
    public void onMouseReleased(int click, int mx, int my)
    {
        enabled = true;
    }

    /*
     * WorldMouseScrollListener
     */

    @Override
    public void onMouseScroll(int value, int mx, int my)
    {
        if (enabled)
        {
            oldScale = getScale();
            if (UtilMath.getSign(value) > 0)
            {
                zoomIn();
            }
            else
            {
                zoomOut();
            }
            updateScrollToCursor(mx, my);
        }
    }
}
