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

import com.b3dgs.lionengine.game.map.TileGame;
import com.b3dgs.lionengine.game.purview.Localizable;

/**
 * Specific tile for platform game.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @param <C> The collision type used.
 */
public abstract class TilePlatform<C extends Enum<C>>
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
    public abstract Double getCollisionX(Localizable localizable);

    /**
     * Get the vertical collision location between the tile and the localizable.
     * 
     * @param localizable The localizable object searching the collision.
     * @return The collision y (<code>null</code> if none).
     */
    public abstract Double getCollisionY(Localizable localizable);

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
}
