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
package com.b3dgs.lionengine.game.collision.tile;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.core.Graphics;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.tile.Tile;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.ImageBuffer;
import com.b3dgs.lionengine.graphic.Transparency;
import com.b3dgs.lionengine.util.UtilMath;

/**
 * Map tile collision model implementation.
 */
public class MapTileCollisionRendererModel extends FeatureModel implements MapTileCollisionRenderer
{
    /**
     * Create the function draw to buffer.
     * 
     * @param collision The collision reference.
     * @param tw The tile width.
     * @param th The tile height.
     * @return The created collision representation buffer.
     */
    public static ImageBuffer createFunctionDraw(CollisionFormula collision, int tw, int th)
    {
        final ImageBuffer buffer = Graphics.createImageBuffer(tw, th, Transparency.TRANSLUCENT);
        final Graphic g = buffer.createGraphic();
        g.setColor(ColorRgba.PURPLE);

        createFunctionDraw(g, collision, tw, th);

        g.dispose();
        return buffer;
    }

    /**
     * Create the function draw to buffer by computing all possible locations.
     * 
     * @param g The graphic buffer.
     * @param formula The collision formula.
     * @param tw The tile width.
     * @param th The tile height.
     */
    private static void createFunctionDraw(Graphic g, CollisionFormula formula, int tw, int th)
    {
        for (int x = 0; x < tw; x++)
        {
            for (int y = 0; y < th; y++)
            {
                renderCollision(g, formula, th, x, y);
            }
        }
    }

    /**
     * Render collision from current vector.
     * 
     * @param g The graphic buffer.
     * @param formula The collision formula.
     * @param th The tile height.
     * @param x The current horizontal location.
     * @param y The current vertical location.
     */
    private static void renderCollision(Graphic g, CollisionFormula formula, int th, int x, int y)
    {
        final CollisionFunction function = formula.getFunction();
        final CollisionRange range = formula.getRange();
        switch (range.getOutput())
        {
            case X:
                renderX(g, function, range, th, x, y);
                break;
            case Y:
                renderY(g, function, range, th, x, y);
                break;
            default:
                throw new LionEngineException(range.getOutput());
        }
    }

    /**
     * Render horizontal collision from current vector.
     * 
     * @param g The graphic buffer.
     * @param function The collision function.
     * @param range The collision range.
     * @param th The tile height.
     * @param x The current horizontal location.
     * @param y The current vertical location.
     */
    private static void renderX(Graphic g, CollisionFunction function, CollisionRange range, int th, int x, int y)
    {
        final double fx = function.compute(y);
        if (UtilMath.isBetween(x, range.getMinX(), range.getMaxX())
            && UtilMath.isBetween(y, range.getMinY(), range.getMaxY()))
        {
            g.drawRect((int) fx, th - y - 1, 0, 0, false);
        }
    }

    /**
     * Render vertical collision from current vector.
     * 
     * @param g The graphic buffer.
     * @param function The collision function.
     * @param range The collision range.
     * @param th The tile height.
     * @param x The current horizontal location.
     * @param y The current vertical location.
     */
    private static void renderY(Graphic g, CollisionFunction function, CollisionRange range, int th, int x, int y)
    {
        final double fy = function.compute(x);
        if (UtilMath.isBetween(y, range.getMinY(), range.getMaxY())
            && UtilMath.isBetween(x, range.getMinX(), range.getMaxX()))
        {
            g.drawRect(x, th - (int) fy - 1, 0, 0, false);
        }
    }

    /** Map reference. */
    private MapTile map;
    /** Map collision reference. */
    private MapTileCollision mapCollision;
    /** Collision draw cache. */
    private Map<CollisionFormula, ImageBuffer> collisionCache;

    /**
     * Create the map tile collision.
     * <p>
     * The {@link Services} must provide the following services:
     * </p>
     * <ul>
     * <li>{@link MapTile}</li>
     * <li>{@link MapTileCollision}</li>
     * </ul>
     */
    public MapTileCollisionRendererModel()
    {
        super();
    }

    /**
     * Render the collision function.
     * 
     * @param g The graphic output.
     * @param tile The tile reference.
     * @param x The horizontal render location.
     * @param y The vertical render location.
     */
    private void renderCollision(Graphic g, TileCollision tile, int x, int y)
    {
        for (final CollisionFormula collision : tile.getCollisionFormulas())
        {
            final ImageBuffer buffer = collisionCache.get(collision);
            if (buffer != null)
            {
                // x - 1 because collision result is outside tile area
                g.drawImage(buffer, x - 1, y);
            }
        }
    }

    /*
     * MapTileCollisionRenderer
     */

    @Override
    public void prepare(Featurable owner, Services services)
    {
        super.prepare(owner, services);

        map = services.get(MapTile.class);
        mapCollision = map.getFeature(MapTileCollision.class);
    }

    @Override
    public void createCollisionDraw()
    {
        clearCollisionDraw();

        final Collection<CollisionFormula> formulas = mapCollision.getCollisionFormulas();
        collisionCache = new HashMap<CollisionFormula, ImageBuffer>(formulas.size());

        for (final CollisionFormula collision : formulas)
        {
            final ImageBuffer buffer = createFunctionDraw(collision, map.getTileWidth(), map.getTileHeight());
            collisionCache.put(collision, buffer);
        }
    }

    @Override
    public void clearCollisionDraw()
    {
        if (collisionCache != null)
        {
            for (final ImageBuffer buffer : collisionCache.values())
            {
                buffer.dispose();
            }
            collisionCache.clear();
            collisionCache = null;
        }
    }

    @Override
    public void renderTile(Graphic g, MapTile map, Tile tile, int x, int y)
    {
        if (tile.hasFeature(TileCollision.class))
        {
            renderCollision(g, tile.getFeature(TileCollision.class), x, y);
        }
    }
}
