/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.platform.map;

import java.util.Set;

import com.b3dgs.lionengine.core.UtilityMath;
import com.b3dgs.lionengine.game.map.TileGame;
import com.b3dgs.lionengine.game.platform.CollisionFunction;
import com.b3dgs.lionengine.game.platform.CollisionRefential;
import com.b3dgs.lionengine.game.platform.CollisionTile;
import com.b3dgs.lionengine.game.purview.Localizable;

/**
 * Specific tile for platform game.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @param <C> The collision type used.
 */
public class TilePlatform<C extends Enum<C> & CollisionTile>
        extends TileGame<C>
{
    /**
     * Constructor.
     * 
     * @param width The tile width.
     * @param height The tile height.
     * @param pattern The tile pattern.
     * @param number The tile number.
     * @param collision The tile collision.
     */
    public TilePlatform(int width, int height, Integer pattern, int number, C collision)
    {
        super(width, height, pattern, number, collision);
    }

    /**
     * Get the horizontal collision location between the tile and the localizable.
     * 
     * @param localizable The localizable object searching the collision.
     * @return The collision x (<code>null</code> if none).
     */
    public Double getCollisionX(Localizable localizable)
    {
        final C collision = getCollision();
        final Set<CollisionFunction> collisionFunctions = collision.getCollisionFunctions();

        for (final CollisionFunction function : collisionFunctions)
        {
            if (function.getAxis() == CollisionRefential.X)
            {
                final int min = function.getRange().getMin();
                final int max = function.getRange().getMax();
                final int x = getInputValue(function, localizable);
                if (x >= min && x <= max)
                {
                    final double value = getX() + function.computeCollision(x);
                    if (localizable.getLocationOldX() >= value && localizable.getLocationX() <= value
                            || localizable.getLocationX() >= value && localizable.getLocationOldX() <= value)
                    {
                        return Double.valueOf(value);
                    }
                }
            }
        }
        return null;
    }

    /**
     * Get the vertical collision location between the tile and the localizable.
     * 
     * @param localizable The localizable object searching the collision.
     * @return The collision y (<code>null</code> if none).
     */
    public Double getCollisionY(Localizable localizable)
    {
        final C collision = getCollision();
        final Set<CollisionFunction> collisionFunctions = collision.getCollisionFunctions();

        for (final CollisionFunction function : collisionFunctions)
        {
            if (function.getAxis() == CollisionRefential.Y)
            {
                final int min = function.getRange().getMin();
                final int max = function.getRange().getMax();
                final int x = getInputValue(function, localizable);
                if (x >= min && x <= max)
                {
                    final double margin = Math.ceil(Math.abs((localizable.getLocationOldX() - localizable
                            .getLocationX()) * function.getValue())) + 1;
                    final double value = getY() + function.computeCollision(x);
                    if (localizable.getLocationOldY() >= value - margin && localizable.getLocationY() <= value + margin)
                    {
                        return Double.valueOf(value);
                    }
                }
            }
        }
        return null;
    }

    /**
     * Check if there is a collision between the localizable and the tile.
     * 
     * @param localizable The localizable.
     * @return <code>true</code> if collide, <code>false</code> else.
     */
    public boolean hasCollision(Localizable localizable)
    {
        return getCollision() != null && (getCollisionX(localizable) != null || getCollisionY(localizable) != null);
    }

    /**
     * Get the input value from the function.
     * 
     * @param function The function used.
     * @param localizable The localizable reference.
     * @return The input value.
     */
    private int getInputValue(CollisionFunction function, Localizable localizable)
    {
        final CollisionRefential input = function.getInput();
        switch (input)
        {
            case X:
                return UtilityMath.fixBetween(localizable.getLocationIntX() - getX(), 0, getWidth() - 1);
            case Y:
                return UtilityMath.fixBetween(localizable.getLocationIntY() - getY(), 0, getHeight() - 1);
            default:
                throw new RuntimeException("Unknow type: " + input);
        }
    }
}
