/*
 * Copyright (C) 2013-2021 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.ListenableModel;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;

/**
 * Tile collidable model implementation.
 */
public class TileCollidableModel extends FeatureModel implements TileCollidable, Recyclable
{
    /** Launcher listeners. */
    private final ListenableModel<TileCollidableListener> listenable = new ListenableModel<>();
    /** Computed results. */
    private final Map<String, CollisionResult> results = new HashMap<>();
    /** Map tile reference. */
    private final MapTileCollision map;
    /** Enabled flags. */
    private final Map<Axis, Boolean> enabledAxis = new EnumMap<>(Axis.class);
    /** The collisions used. */
    private final Collection<CollisionCategory> categories;
    /** Collision enabled. */
    private boolean enabled;

    /** Transformable owning this model. */
    private Transformable transformable;

    /**
     * Create feature.
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
     * The {@link Configurer} must provide a valid {@link CollisionCategoryConfig}.
     * </p>
     * <p>
     * If the {@link Featurable} is a {@link TileCollidableListener}, it will automatically
     * {@link #addListener(TileCollidableListener)} on it.
     * </p>
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public TileCollidableModel(Services services, Setup setup)
    {
        super(services, setup);

        if (setup.hasNode(CollisionCategoryConfig.NODE_CATEGORIES))
        {
            map = services.get(MapTile.class).getFeature(MapTileCollision.class);
            categories = CollisionCategoryConfig.imports(setup, map);
        }
        else
        {
            map = null;
            categories = Collections.emptyList();
            enabled = false;
        }
    }

    /**
     * Update the tile collision computation.
     * 
     * @param category The collision category reference.
     */
    private void update(CollisionCategory category)
    {
        final CollisionResult result = results.get(category.getName());

        if (result != null
            && (result.getX() != null || result.getY() != null)
            && Boolean.TRUE.equals(enabledAxis.get(category.getAxis())))
        {
            onCollided(result, category);
        }
    }

    /**
     * Called when a collision occurred on a specified axis.
     * 
     * @param result The result reference.
     * @param category The collision category reference.
     */
    private void onCollided(CollisionResult result, CollisionCategory category)
    {
        for (int i = 0; i < listenable.size(); i++)
        {
            listenable.get(i).notifyTileCollided(result, category);
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
        listenable.addListener(listener);
    }

    @Override
    public void removeListener(TileCollidableListener listener)
    {
        listenable.removeListener(listener);
    }

    @Override
    public void update(double extrp)
    {
        if (enabled)
        {
            results.clear();
            for (final CollisionCategory category : categories)
            {
                final CollisionResult result = map.computeCollision(transformable, category);
                results.put(category.getName(), result);
            }
            for (final CollisionCategory category : categories)
            {
                update(category);
            }
        }
    }

    @Override
    public void apply(CollisionResult result)
    {
        if (result.getX() != null)
        {
            transformable.teleportX(result.getX().doubleValue());
        }
        if (result.getY() != null)
        {
            transformable.teleportY(result.getY().doubleValue());
        }
    }

    @Override
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    @Override
    public void setEnabled(Axis axis, boolean enabled)
    {
        enabledAxis.put(axis, Boolean.valueOf(enabled));
    }

    @Override
    public Collection<CollisionCategory> getCategories()
    {
        return Collections.unmodifiableCollection(categories);
    }

    /*
     * Recyclable
     */

    @Override
    public void recycle()
    {
        enabled = true;
        for (final Axis axis : Axis.values())
        {
            enabledAxis.put(axis, Boolean.TRUE);
        }
    }
}
