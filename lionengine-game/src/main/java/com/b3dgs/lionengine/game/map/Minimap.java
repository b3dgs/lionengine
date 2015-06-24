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

import java.util.Map;

import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Localizable;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Transparency;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.Graphics;
import com.b3dgs.lionengine.core.ImageBuffer;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.drawable.Image;
import com.b3dgs.lionengine.game.configurer.ConfigMinimap;
import com.b3dgs.lionengine.stream.Stream;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Minimap representation of a map tile. This can be used to represent strategic view of a map.
 * <p>
 * A call to {@link #load()} is needed once the {@link MapTile} as been created / loaded, and also each time
 * modification occurs on the map.
 * </p>
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see MapTile
 */
public class Minimap
        implements Image
{
    /** Map reference. */
    private final MapTile map;
    /** Pixel configuration. */
    private Map<String, ColorRgba> pixels;
    /** Minimap image reference. */
    private ImageBuffer minimap;
    /** Origin reference. */
    private Origin origin;
    /** Horizontal location. */
    private double x;
    /** Vertical location. */
    private double y;
    /** Alpha. */
    private boolean alpha;

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
     * Set the pixel configuration to use.
     * 
     * @param config The pixel configuration file.
     */
    public void loadPixelConfig(Media config)
    {
        final XmlNode root = Stream.loadXml(config);
        pixels = ConfigMinimap.create(root, map);
    }

    /*
     * Image
     */

    @Override
    public void load() throws LionEngineException
    {
        // Nothing to do
    }

    @Override
    public void prepare() throws LionEngineException
    {
        if (minimap == null)
        {
            minimap = Graphics.createImageBuffer(map.getInTileWidth(), map.getInTileHeight(), alpha
                    ? Transparency.TRANSLUCENT : Transparency.OPAQUE);
        }
        final Graphic g = minimap.createGraphic();
        final int vert = map.getInTileHeight();
        final int hori = map.getInTileWidth();

        for (int ty = 0; ty < vert; ty++)
        {
            for (int tx = 0; tx < hori; tx++)
            {
                final Tile tile = map.getTile(tx, ty);
                if (tile != null)
                {
                    if (pixels != null && tile.getGroup() != null && pixels.containsKey(tile.getGroup()))
                    {
                        g.setColor(pixels.get(tile.getGroup()));
                    }
                    else
                    {
                        g.setColor(ColorRgba.WHITE);
                    }
                }
                else
                {
                    g.setColor(ColorRgba.BLACK);
                }
                g.drawRect(tx, vert - ty - 1, 1, 1, true);
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
    public void setLocation(Viewer viewer, Localizable localizable)
    {
        setLocation(viewer.getViewpointX(localizable.getX()), viewer.getViewpointY(localizable.getY()));
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

    @Override
    public boolean isLoaded()
    {
        return minimap != null;
    }
}
