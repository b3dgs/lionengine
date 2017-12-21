/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.tile.map.collision;

import java.util.Collection;
import java.util.HashSet;

import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.FeatureProvider;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.tile.Tile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;

/**
 * Tile collidable model implementation.
 */
public class TileCollidableModel extends FeatureModel implements TileCollidable, Recyclable
{
    /** Launcher listeners. */
    private final Collection<TileCollidableListener> listeners = new HashSet<TileCollidableListener>();
    /** Transformable owning this model. */
    private Transformable transformable;
    /** The collisions used. */
    private final Collection<CollisionCategory> categories;
    /** Map tile reference. */
    private final MapTileCollision map;
    /** Collision enabled. */
    private boolean enabled;

    /**
     * Create a tile collidable model.
     * <p>
     * The {@link Services} must provide:
     * </p>
     * <ul>
     * <li>{@link MapTileCollision}</li>
     * </ul>
     * <p>
     * The {@link Featurable} must have:
     * </p>
     * <ul>
     * <li>{@link Transformable}</li>
     * </ul>
     * <p>
     * The {@link Setup} must provide a valid {@link CollisionCategoryConfig}.
     * </p>
     * <p>
     * If the {@link Featurable} is a {@link TileCollidableListener}, it will automatically
     * {@link #addListener(TileCollidableListener)} on it.
     * </p>
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    public TileCollidableModel(Services services, Setup setup)
    {
        super();

        map = services.get(MapTile.class).getFeature(MapTileCollision.class);
        categories = CollisionCategoryConfig.imports(setup, map);

        recycle();
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
    public void prepare(FeatureProvider provider)
    {
        transformable = provider.getFeature(Transformable.class);

        if (provider instanceof TileCollidableListener)
        {
            addListener((TileCollidableListener) provider);
        }
    }

    @Override
    public void checkListener(Object listener)
    {
        super.checkListener(listener);

        if (listener instanceof TileCollidableListener)
        {
            addListener((TileCollidableListener) listener);
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

    /*
     * Recyclable
     */

    @Override
    public final void recycle()
    {
        enabled = true;
    }
}
