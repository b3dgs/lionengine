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

import java.util.HashMap;
import java.util.Map;

import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.ImageBuffer;
import com.b3dgs.lionengine.Localizable;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Transparency;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.core.Graphics;
import com.b3dgs.lionengine.drawable.Image;
import com.b3dgs.lionengine.drawable.SpriteTiled;
import com.b3dgs.lionengine.game.configurer.ConfigMinimap;

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

    /**
     * Get the weighted color of an area.
     * 
     * @param surface The surface reference.
     * @param sx The starting horizontal location.
     * @param sy The starting vertical location.
     * @param width The area width.
     * @param height The area height.
     * @return The weighted color.
     */
    public static ColorRgba getWeightedColor(ImageBuffer surface, int sx, int sy, int width, int height)
    {
        int r = 0;
        int g = 0;
        int b = 0;
        int count = 0;
        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                final ColorRgba color = new ColorRgba(surface.getRgb(sx + x, sy + y));
                if (!ColorRgba.TRANSPARENT.equals(color))
                {
                    r += color.getRed();
                    g += color.getGreen();
                    b += color.getBlue();
                    count++;
                }
            }
        }
        if (count == 0)
        {
            return ColorRgba.TRANSPARENT;
        }
        return new ColorRgba(r / count, g / count, b / count);
    }

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
        pixels = ConfigMinimap.imports(config);
    }

    /**
     * Perform an automatic color minimap resolution.
     * 
     * @param config The pixel configuration destination file (can be <code>null</code>).
     */
    public void automaticColor(Media config)
    {
        final Map<TileRef, ColorRgba> colors = new HashMap<TileRef, ColorRgba>();
        for (final Integer sheet : map.getSheets())
        {
            computeSheet(colors, sheet);
        }
        if (config != null)
        {
            ConfigMinimap.exports(config, colors);
        }
        pixels = colors;
        prepare();
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

    /**
     * Compute the current sheet.
     * 
     * @param colors The colors data.
     * @param sheet The sheet number.
     */
    private void computeSheet(Map<TileRef, ColorRgba> colors, Integer sheet)
    {
        final SpriteTiled tiles = map.getSheet(sheet);
        final ImageBuffer surface = tiles.getSurface();
        final int tw = map.getTileWidth();
        final int th = map.getTileHeight();

        int number = 0;
        for (int i = 0; i < surface.getWidth(); i += tw)
        {
            for (int j = 0; j < surface.getHeight(); j += th)
            {
                final int h = number * tw % tiles.getWidth();
                final int v = number / tiles.getTilesHorizontal() * th;
                final ColorRgba color = getWeightedColor(surface, h, v, tw, th);

                if (!Minimap.NO_TILE.equals(color) && !ColorRgba.TRANSPARENT.equals(color))
                {
                    colors.put(new TileRef(sheet, number), color);
                }
                number++;
            }
        }
    }

    /*
     * Image
     */

    @Override
    public void load()
    {
        create();
    }

    @Override
    public void prepare()
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
