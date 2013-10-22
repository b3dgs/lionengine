/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.example.tyrian.map;

import com.b3dgs.lionengine.example.tyrian.entity.Entity;
import com.b3dgs.lionengine.example.tyrian.entity.HandlerEntity;
import com.b3dgs.lionengine.example.tyrian.entity.scenery.EntitySceneryType;
import com.b3dgs.lionengine.example.tyrian.entity.scenery.FactoryEntityScenery;
import com.b3dgs.lionengine.game.map.MapTileGame;

/**
 * Map implementation.
 */
public final class Map
        extends MapTileGame<TileCollision, Tile>
{
    /**
     * Create the entity from the tile number.
     * 
     * @param factory The entity static factory.
     * @param n The tile number.
     * @return The entity instance, <code>null</code> if none.
     */
    private static Entity create(FactoryEntityScenery factory, int n)
    {
        switch (n)
        {
            case 6:
                return factory.create(EntitySceneryType.SINGLE_CANON);
            case 7:
                return factory.create(EntitySceneryType.DOUBLE_CANON);
            case 8:
                return factory.create(EntitySceneryType.RED_ENGINE);
            case 9:
                return factory.create(EntitySceneryType.GREEN_PULSE);
            case 19:
                return factory.create(EntitySceneryType.OPENABLE_PULSE);
            case 13:
                return factory.create(EntitySceneryType.GENERATOR);
            case 26:
                return factory.create(EntitySceneryType.DOUBLE_PULSE);
            case 18:
                return factory.create(EntitySceneryType.LAMP);
            case 30:
                return factory.create(EntitySceneryType.SPIDER);
            case 32:
                return factory.create(EntitySceneryType.SUB_SQUARE);
            case 45:
                return factory.create(EntitySceneryType.SILO);
            case 46:
                return factory.create(EntitySceneryType.RED_BOX);
            case 56:
                return factory.create(EntitySceneryType.THREE_RED_V);
            case 50:
                return factory.create(EntitySceneryType.PUMP);
            case 52:
                return factory.create(EntitySceneryType.HOUSE92);
            case 54:
                return factory.create(EntitySceneryType.HOUSE812);
            case 66:
                return factory.create(EntitySceneryType.THREE_RED_H);
            default:
                return null;
        }
    }

    /**
     * Constructor.
     */
    public Map()
    {
        super(24, 28);
    }

    /**
     * Create static entities depending of the tile.
     * 
     * @param factoryEntityStatic The entity static factory.
     * @param handlerEntity The handler entity.
     */
    public void spawnEntityStatic(FactoryEntityScenery factoryEntityStatic, HandlerEntity handlerEntity)
    {
        for (int tx = 0; tx < widthInTile; tx++)
        {
            for (int ty = 0; ty < heightInTile; ty++)
            {
                final Tile tile = getTile(tx, ty);
                if (tile != null)
                {
                    final int n = tile.getNumber();
                    final Entity entity = Map.create(factoryEntityStatic, n);
                    if (entity != null)
                    {
                        entity.teleport(tile.getX(), tile.getY() + 28);
                        handlerEntity.add(entity);
                    }
                }
            }
        }
    }

    /*
     * MapTileShmup
     */

    @Override
    public Tile createTile(int width, int height, Integer pattern, int number, TileCollision collision)
    {
        return new Tile(width, height, pattern, number, collision);
    }

    @Override
    public TileCollision getCollisionFrom(String collision)
    {
        return TileCollision.NONE;
    }
}
