/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.editor.UtilEclipse;
import com.b3dgs.lionengine.editor.world.WorldMouseScrollListener;
import com.b3dgs.lionengine.editor.world.WorldViewPart;
import com.b3dgs.lionengine.editor.world.ZoomItem;
import com.b3dgs.lionengine.game.Camera;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.object.Services;

/**
 * Handle the world zoom capability.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class WorldZoom implements WorldMouseScrollListener
{
    /** Default zoom value. */
    public static final int ZOOM_DEFAULT = 100;
    /** Maximum zoom value. */
    public static final int ZOOM_MAX = 500;
    /** Minimum zoom value. */
    public static final int ZOOM_MIN = 20;

    /** Camera reference. */
    private final Camera camera;
    /** Map reference. */
    private final MapTile map;
    /** Zoom level in percent. */
    private int zoom = ZOOM_DEFAULT;
    /** Old scale. */
    private double oldScale;

    /**
     * Create the world zoom.
     * 
     * @param services The services reference.
     */
    public WorldZoom(Services services)
    {
        camera = services.get(Camera.class);
        map = services.get(MapTile.class);
    }

    /**
     * Set the zoom percent value.
     * 
     * @param percent The new zoom percent value.
     */
    public void setPercent(int percent)
    {
        zoom = UtilMath.fixBetween(percent, ZOOM_MIN, ZOOM_MAX);
    }

    /**
     * Get the zoom percent value.
     * 
     * @return The zoom percent value.
     */
    public int getPercent()
    {
        return zoom;
    }

    /**
     * Get the zoom scale.
     * 
     * @return The zoom scale.
     */
    public double getScale()
    {
        final double realScale = zoom / 100.0;
        final double scale = Math.round(map.getTileWidth() * realScale) / (double) map.getTileWidth();
        return scale;
    }

    /**
     * Lock scroll to cursor.
     * 
     * @param mx The current horizontal location.
     * @param my The current vertical location.
     */
    private void updateScrollToCursor(int mx, int my)
    {
        final int tw = map.getTileWidth();
        final int th = map.getTileHeight();

        final double scaleDiff = getScale() - oldScale;

        final double ox;
        final double oy;
        if (scaleDiff > 0)
        {
            ox = Origin.BOTTOM_LEFT.getX(mx, camera.getWidth()) * scaleDiff;
            oy = Origin.BOTTOM_LEFT.getY(my, camera.getHeight()) * scaleDiff;
        }
        else
        {
            ox = Origin.MIDDLE.getX(mx, camera.getWidth()) * scaleDiff;
            oy = Origin.MIDDLE.getY(my, camera.getHeight()) * scaleDiff;
        }
        camera.teleport(UtilMath.getRounded(camera.getX() + ox, tw), UtilMath.getRounded(camera.getY() - oy, th));
    }

    /*
     * WorldMouseScrollListener
     */

    @Override
    public void onMouseScroll(int value, int mx, int my)
    {
        oldScale = getScale();
        zoom = UtilMath.fixBetween(zoom + (int) Math.ceil(zoom * value / 100.0), ZOOM_MIN, ZOOM_MAX);

        final WorldViewPart part = UtilEclipse.getPart(WorldViewPart.ID, WorldViewPart.class);
        part.setToolItemText(ZoomItem.ID, String.valueOf(zoom));
        updateScrollToCursor(mx, my);
    }
}
