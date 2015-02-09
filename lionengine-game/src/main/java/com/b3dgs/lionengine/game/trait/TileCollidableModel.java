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
package com.b3dgs.lionengine.game.trait;

import java.util.Collection;
import java.util.HashSet;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.Axis;
import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.collision.CollisionCategory;
import com.b3dgs.lionengine.game.collision.CollisionResult;
import com.b3dgs.lionengine.game.configurer.ConfigCollisionCategory;
import com.b3dgs.lionengine.game.configurer.Configurer;
import com.b3dgs.lionengine.game.handler.ObjectGame;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.map.MapTileCollision;
import com.b3dgs.lionengine.game.map.Tile;

/**
 * Tile collidable model implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class TileCollidableModel
        extends TraitModel
        implements TileCollidable
{
    /** Launcher listeners. */
    private final Collection<TileCollidableListener> listeners;
    /** Transformable owning this model. */
    private final Transformable transformable;
    /** Body optional reference (<code>null</code> if none). */
    private final Body body;
    /** The collisions used. */
    private final Collection<CollisionCategory> categories;
    /** Map tile reference. */
    private final MapTileCollision map;

    /**
     * Create a tile collidable model.
     * <p>
     * The owner must have the following {@link Trait}:
     * </p>
     * <ul>
     * <li>{@link Transformable}</li>
     * <li>{@link Body} (optional)</li>
     * </ul>
     * The {@link Services} must provide the following services:
     * </p>
     * <ul>
     * <li>{@link MapTile}</li>
     * </ul>
     * 
     * @param owner The owner reference.
     * @param configurer The configurer reference.
     * @param services The services reference.
     * @throws LionEngineException If missing {@link Trait}.
     */
    public TileCollidableModel(ObjectGame owner, Configurer configurer, Services services) throws LionEngineException
    {
        super(owner);
        listeners = new HashSet<>();
        transformable = getTrait(Transformable.class);
        body = owner.getTrait(Body.class);
        map = services.get(MapTileCollision.class);
        categories = ConfigCollisionCategory.create(configurer, map);
    }

    /**
     * Update the collision tile category.
     * 
     * @param category The category reference.
     */
    private void update(CollisionCategory category)
    {
        final CollisionResult result = map.computeCollision(transformable, category);
        if (result != null)
        {
            if (result.getX() != null)
            {
                transformable.teleportX(result.getX().doubleValue());
                onCollided(result.getTile(), category.getAxis());
            }
            if (result.getY() != null)
            {
                transformable.teleportY(result.getY().doubleValue());
                if (body != null && Axis.Y == category.getAxis())
                {
                    body.resetGravity();
                }
                onCollided(result.getTile(), category.getAxis());
            }
        }
    }

    /**
     * Called when a collision occurred on a specified axis.
     * 
     * @param tile The tile reference.
     * @param axis The axis reference.
     */
    private void onCollided(Tile tile, Axis axis)
    {
        for (final TileCollidableListener listener : listeners)
        {
            listener.notifyTileCollided(tile, axis);
        }
    }

    /*
     * TileCollidable
     */

    @Override
    public void addListener(TileCollidableListener listener)
    {
        listeners.add(listener);
    }

    @Override
    public void removeListener(TileCollidableListener listener)
    {
        listeners.remove(listener);
    }

    @Override
    public void update(double extrp)
    {
        for (final CollisionCategory category : categories)
        {
            update(category);
        }
    }

    @Override
    public Collection<CollisionCategory> getCategories()
    {
        return categories;
    }
}
