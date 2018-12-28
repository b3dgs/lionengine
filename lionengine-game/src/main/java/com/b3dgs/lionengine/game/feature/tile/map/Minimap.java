/*
 * Copyright (C) 2013-2018 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.tile.map;

import java.util.HashMap;
import java.util.Map;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Localizable;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.game.feature.tile.Tile;
import com.b3dgs.lionengine.game.feature.tile.TileRef;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.ImageBuffer;
import com.b3dgs.lionengine.graphic.UtilColor;
import com.b3dgs.lionengine.graphic.drawable.Image;
import com.b3dgs.lionengine.graphic.drawable.SpriteTiled;

/**
 * Minimap representation of a map tile. This can be used to represent strategic view of a map.
 * <p>
 * Usage:
 * </p>
 * <ul>
 * <li>1. Create minimap : {@link #Minimap(MapTile)}</li>
 * <li>2. Load surface : {@link #load()}</li>
 * <li>3. Generate minimap from map: {@link #automaticColor()} or {@link #automaticColor(Media)}</li>
 * <li>3. Or load from configuration: {@link #loadPixelConfig(Media)}</li>
 * <li>4. Prepare surface : {@link #prepare()}</li>
 * </ul>
 * 
 * @see MapTile
 */
public class Minimap implements Image
{
    /** Surface not loaded error. */
    static final String ERROR_SURFACE = "Surface has not beed loaded !";
    /** No tile representation. */
    private static final ColorRgba NO_TILE = ColorRgba.TRANSPARENT;
    /** Default tile color. */
    private static final ColorRgba DEFAULT_COLOR = ColorRgba.WHITE;

    /** Pixel configuration. */
    private Map<TileRef, ColorRgba> pixels = new HashMap<>();
    /** Map reference. */
    private final MapTile map;
    /** Minimap image reference. */
    private ImageBuffer surface;
    /** Origin reference. */
    private Origin origin = Origin.TOP_LEFT;
    /** Horizontal location. */
    private double x;
    /** Vertical location. */
    private double y;

    /**
     * Create a minimap.
     * 
     * @param map The map reference.
     * @throws LionEngineException If <code>null</code> argument.
     */
    public Minimap(MapTile map)
    {
        super();

        Check.notNull(map);

        this.map = map;
    }

    /**
     * Set the pixel configuration to use. Call {@link #prepare()} to apply configuration.
     * 
     * @param config The pixel configuration file.
     * @throws LionEngineException If <code>null</code> argument.
     */
    public void loadPixelConfig(Media config)
    {
        pixels.clear();
        pixels.putAll(MinimapConfig.imports(config));
    }

    /**
     * Perform an automatic color minimap resolution. Call {@link #prepare()} to apply configuration.
     */
    public void automaticColor()
    {
        final Map<TileRef, ColorRgba> colors = new HashMap<>();
        for (final Integer sheet : map.getSheets())
        {
            computeSheet(colors, sheet);
        }
        pixels = colors;
    }

    /**
     * Perform an automatic color minimap resolution. Call {@link #prepare()} to apply configuration.
     * 
     * @param config The pixel configuration destination file.
     * @throws LionEngineException If <code>null</code> argument.
     */
    public void automaticColor(Media config)
    {
        automaticColor();
        MinimapConfig.exports(config, pixels);
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
        else
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
        final ImageBuffer tilesSurface = tiles.getSurface();
        final int tw = map.getTileWidth();
        final int th = map.getTileHeight();

        int number = 0;
        for (int i = 0; i < tilesSurface.getWidth(); i += tw)
        {
            for (int j = 0; j < tilesSurface.getHeight(); j += th)
            {
                final int h = number * tw % tiles.getWidth();
                final int v = number / tiles.getTilesHorizontal() * th;
                final ColorRgba color = UtilColor.getWeightedColor(tilesSurface, h, v, tw, th);

                if (!(NO_TILE.equals(color) || color.getAlpha() == 0))
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

    /**
     * Load minimap surface from map tile size. Does nothing if already loaded.
     */
    @Override
    public void load()
    {
        if (surface == null)
        {
            surface = Graphics.createImageBuffer(map.getInTileWidth(), map.getInTileHeight(), ColorRgba.TRANSPARENT);
        }
    }

    /**
     * Fill minimap surface with tile color configuration.
     * 
     * @throws LionEngineException If surface has not been loaded ({@link #load()} may have not been called).
     */
    @Override
    public void prepare()
    {
        if (surface == null)
        {
            throw new LionEngineException(ERROR_SURFACE);
        }
        final Graphic g = surface.createGraphic();
        final int v = map.getInTileHeight();
        final int h = map.getInTileWidth();

        for (int ty = 0; ty < v; ty++)
        {
            for (int tx = 0; tx < h; tx++)
            {
                final Tile tile = map.getTile(tx, ty);
                final ColorRgba color = getTileColor(tile);
                if (!NO_TILE.equals(color))
                {
                    g.setColor(color);
                    g.drawRect(tx, v - ty - 1, 1, 1, true);
                }
            }
        }
        g.dispose();
    }

    @Override
    public void dispose()
    {
        surface.dispose();
        pixels.clear();
    }

    @Override
    public void render(Graphic g)
    {
        g.drawImage(surface, (int) x, (int) y);
    }

    @Override
    public void setOrigin(Origin origin)
    {
        Check.notNull(origin);

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
        return surface.getWidth();
    }

    @Override
    public int getHeight()
    {
        return surface.getHeight();
    }

    @Override
    public ImageBuffer getSurface()
    {
        return surface;
    }

    @Override
    public boolean isLoaded()
    {
        return surface != null;
    }
}
