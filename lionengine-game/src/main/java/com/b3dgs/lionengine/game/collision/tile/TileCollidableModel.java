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
import java.util.HashSet;

import com.b3dgs.lionengine.game.Axis;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.game.handler.FeatureModel;
import com.b3dgs.lionengine.game.handler.Handlable;
import com.b3dgs.lionengine.game.handler.Services;
import com.b3dgs.lionengine.game.object.ObjectGame;
import com.b3dgs.lionengine.game.object.Setup;
import com.b3dgs.lionengine.game.object.feature.transformable.Transformable;
import com.b3dgs.lionengine.game.tile.Tile;

/**
 * Tile collidable model implementation.
 */
public class TileCollidableModel extends FeatureModel implements TileCollidable
{
    /** Launcher listeners. */
    private final Collection<TileCollidableListener> listeners = new HashSet<TileCollidableListener>();
    /** Configurer reference. */
    private final Configurer configurer;
    /** Transformable owning this model. */
    private Transformable transformable;
    /** The collisions used. */
    private Collection<CollisionCategory> categories;
    /** Map tile reference. */
    private MapTileCollision map;
    /** Collision enabled. */
    private boolean enabled = true;

    /**
     * Create a tile collidable model.
     * <p>
     * The {@link ObjectGame} owner must have the following {@link com.b3dgs.lionengine.game.handler.Feature}:
     * </p>
     * <ul>
     * <li>{@link Transformable}</li>
     * </ul>
     * <p>
     * The {@link ObjectGame} owner must provide a valid {@link com.b3dgs.lionengine.game.Configurer} compatible
     * with {@link CollisionCategoryConfig}.
     * </p>
     * <p>
     * The {@link Services} must provide the following services:
     * </p>
     * <ul>
     * <li>{@link com.b3dgs.lionengine.game.map.MapTile}</li>
     * </ul>
     * <p>
     * If the {@link ObjectGame} is a {@link TileCollidableListener}, it will automatically
     * {@link #addListener(TileCollidableListener)} on it.
     * </p>
     * 
     * @param setup The setup reference.
     */
    public TileCollidableModel(Setup setup)
    {
        super();
        configurer = setup.getConfigurer();
    }

    /**
     * Update the tile collision computation.
     * 
     * @param category The collision category reference.
     */
    private void update(CollisionCategory category)
    {
        final CollisionResult result = map.computeCollision(transformable, category);
        if (result != null)
        {
            if (result.getX() != null)
            {
                onCollided(result.getTile(), category.getAxis());
                transformable.teleportX(result.getX().doubleValue());
            }
            if (result.getY() != null)
            {
                onCollided(result.getTile(), category.getAxis());
                transformable.teleportY(result.getY().doubleValue());
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
    public void prepare(Handlable owner, Services services)
    {
        transformable = owner.getFeature(Transformable.class);
        map = services.get(MapTileCollision.class);
        categories = CollisionCategoryConfig.imports(configurer, map);

        if (owner instanceof TileCollidableListener)
        {
            addListener((TileCollidableListener) owner);
        }
    }

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
        if (enabled)
        {
            for (final CollisionCategory category : categories)
            {
                update(category);
            }
        }
    }

    @Override
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    @Override
    public Collection<CollisionCategory> getCategories()
    {
        return categories;
    }
}
