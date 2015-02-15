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
package com.b3dgs.lionengine.game.map;

import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Transparency;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.ImageBuffer;
import com.b3dgs.lionengine.drawable.Image;

/**
 * Minimap layer representation of a map tile.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class Minimap
        implements Image
{
    /** Map reference. */
    private final MapTile map;
    /** Minimap reference. */
    private ImageBuffer minimap;
    /** Origin reference. */
    private Origin origin;
    /** Horizontal location. */
    private double x;
    /** Vertical location. */
    private double y;

    /**
     * Create a minimap.
     * 
     * @param map The map reference.
     */
    public Minimap(MapTile map)
    {
        this.map = map;
        origin = Origin.TOP_LEFT;
    }

    /**
     * Get color corresponding to the specified tile. Override it to return a specific color for each type of tile.
     * This function is used when generating the minimap.
     * 
     * @param tile The input tile.
     * @return The color representing the tile on minimap.
     */
    protected ColorRgba getTilePixelColor(Tile tile)
    {
        return ColorRgba.WHITE;
    }

    /*
     * Image
     */

    @Override
    public void load(boolean alpha) throws LionEngineException
    {
        if (minimap == null)
        {
            minimap = Core.GRAPHIC.createImageBuffer(map.getWidthInTile(), map.getHeightInTile(), alpha
                    ? Transparency.TRANSLUCENT : Transparency.OPAQUE);
        }
        final Graphic g = minimap.createGraphic();
        final int vert = map.getHeightInTile();
        final int hori = map.getWidthInTile();

        for (int v = 0; v < vert; v++)
        {
            for (int h = 0; h < hori; h++)
            {
                final Tile tile = map.getTile(h, v);
                if (tile != null)
                {
                    g.setColor(getTilePixelColor(tile));
                }
                else
                {
                    g.setColor(ColorRgba.BLACK);
                }
                g.drawRect(h, vert - v - 1, 1, 1, true);
            }
        }
        g.dispose();
    }

    @Override
    public void render(Graphic g)
    {
        g.drawImage(minimap, (int) x, (int) y);
    }

    @Override
    public void setOrigin(Origin origin)
    {
        this.origin = origin;
    }

    @Override
    public void setLocation(double x, double y)
    {
        this.x = origin.getX(x, getWidth());
        this.y = origin.getY(y, getHeight());
    }

    @Override
    public void setLocation(Viewer viewer, double x, double y)
    {
        setLocation(viewer.getViewpointX(x), viewer.getViewpointY(y));
    }

    @Override
    public double getX()
    {
        return x;
    }

    @Override
    public double getY()
    {
        return y;
    }

    @Override
    public int getWidth()
    {
        return minimap.getWidth();
    }

    @Override
    public int getHeight()
    {
        return minimap.getHeight();
    }

    @Override
    public ImageBuffer getSurface()
    {
        return minimap;
    }
}
