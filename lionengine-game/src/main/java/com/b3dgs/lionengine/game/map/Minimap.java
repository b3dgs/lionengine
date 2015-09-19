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
import com.b3dgs.lionengine.game.collision.TileGroup.TileRef;
import com.b3dgs.lionengine.game.configurer.ConfigMinimap;
import com.b3dgs.lionengine.stream.Stream;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Minimap representation of a map tile. This can be used to represent strategic view of a map.
 * <p>
 * A call to {@link #prepare()} is needed once the {@link MapTile} as been created / loaded, and also each time
 * modification occurs on the map.
 * </p>
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see MapTile
 */
public class Minimap implements Image
{
    /** No tile representation. */
    public static final ColorRgba NO_TILE = ColorRgba.BLACK;
    /** Default tile color. */
    private static final ColorRgba DEFAULT_COLOR = ColorRgba.WHITE;

    /** Map reference. */
    private final MapTile map;
    /** Pixel configuration. */
    private Map<TileRef, ColorRgba> pixels;
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

    /**
     * Create the minimap if not already created.
     */
    private void create()
    {
        if (minimap == null)
        {
            final Transparency transparency;
            if (alpha)
            {
                transparency = Transparency.TRANSLUCENT;
            }
            else
            {
                transparency = Transparency.OPAQUE;
            }
            minimap = Graphics.createImageBuffer(map.getInTileWidth(), map.getInTileHeight(), transparency);
        }
    }

    /**
     * Get the corresponding tile color.
     * 
     * @param tile The tile reference.
     * @return The tile color representation.
     */
    private ColorRgba getTileColor(Tile tile)
    {
        final ColorRgba color;
        if (tile == null)
        {
            color = NO_TILE;
        }
        else if (pixels != null)
        {
            final TileRef ref = new TileRef(tile.getSheet(), tile.getNumber());
            if (!pixels.containsKey(ref))
            {
                color = DEFAULT_COLOR;
            }
            else
            {
                color = pixels.get(ref);
            }
        }
        else
        {
            color = DEFAULT_COLOR;
        }
        return color;
    }

    /*
     * Image
     */

    @Override
    public void load() throws LionEngineException
    {
        create();
    }

    @Override
    public void prepare() throws LionEngineException
    {
        final Graphic g = minimap.createGraphic();
        final int v = map.getInTileHeight();
        final int h = map.getInTileWidth();

        for (int ty = 0; ty < v; ty++)
        {
            for (int tx = 0; tx < h; tx++)
            {
                final Tile tile = map.getTile(tx, ty);
                g.setColor(getTileColor(tile));
                g.drawRect(tx, v - ty - 1, 1, 1, true);
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
