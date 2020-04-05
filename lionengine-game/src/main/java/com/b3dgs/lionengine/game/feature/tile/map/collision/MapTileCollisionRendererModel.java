/*
 * Copyright (C) 2013-2020 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.tile.map.collision;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.feature.FeatureAbstract;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.tile.Tile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.ImageBuffer;

/**
 * Map tile collision model implementation.
 */
public class MapTileCollisionRendererModel extends FeatureAbstract implements MapTileCollisionRenderer
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
        final ImageBuffer buffer = Graphics.createImageBuffer(tw, th, ColorRgba.TRANSPARENT);
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
                renderX(g, function, range, th, y);
                break;
            case Y:
                renderY(g, function, range, th, x);
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
     * @param y The current vertical location.
     */
    private static void renderX(Graphic g, CollisionFunction function, CollisionRange range, int th, int y)
    {
        if (UtilMath.isBetween(y, range.getMinY(), range.getMaxY()))
        {
            g.drawRect(function.getRenderX(y), th - y - 1, 0, 0, false);
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
     */
    private static void renderY(Graphic g, CollisionFunction function, CollisionRange range, int th, int x)
    {
        if (UtilMath.isBetween(x, range.getMinX(), range.getMaxX()))
        {
            g.drawRect(x, th - function.getRenderY(x) - 1, 0, 0, false);
        }
    }

    /** Map reference. */
    private final MapTile map;
    /** Map collision reference. */
    private final MapTileCollision mapCollision;
    /** Collision draw cache. */
    private Map<CollisionFormula, ImageBuffer> collisionCache;

    /**
     * Create feature.
     * <p>
     * The {@link Services} must provide the following services:
     * </p>
     * <ul>
     * <li>{@link MapTile}</li>
     * <li>{@link MapTileCollision}</li>
     * </ul>
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    public MapTileCollisionRendererModel(Services services)
    {
        super();

        Check.notNull(services);

        map = services.get(MapTile.class);
        mapCollision = map.getFeature(MapTileCollision.class);
    }

    /**
     * Render the collision function.
     * 
     * @param g The graphic output.
     * @param tile The tile reference.
     * @param x The horizontal render location.
     * @param y The vertical render location.
     */
    private void renderCollision(Graphic g, Tile tile, int x, int y)
    {
        for (final CollisionFormula collision : mapCollision.getCollisionFormulas(tile))
        {
            final ImageBuffer buffer = collisionCache.get(collision);
            if (buffer != null)
            {
                g.drawImage(buffer, x, y);
            }
        }
    }

    /*
     * MapTileCollisionRenderer
     */

    @Override
    public void createCollisionDraw()
    {
        clearCollisionDraw();

        final Collection<CollisionFormula> formulas = mapCollision.getCollisionFormulas();
        collisionCache = new HashMap<>(formulas.size());

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
    public void renderTile(Graphic g, Tile tile, int x, int y)
    {
        if (collisionCache != null)
        {
            renderCollision(g, tile, x, y);
        }
    }
}
