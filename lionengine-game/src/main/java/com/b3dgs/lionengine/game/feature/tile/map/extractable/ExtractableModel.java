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
package com.b3dgs.lionengine.game.feature.tile.map.extractable;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.Alterable;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;

/**
 * Extractable model implementation.
 */
public class ExtractableModel extends FeatureModel implements Extractable
{
    /** Resources count. */
    private final Alterable resources = new Alterable(Integer.MAX_VALUE);
    /** Map reference. */
    private final MapTile map = services.get(MapTile.class);
    /** Resource type. */
    private String type;

    /** Transformable model. */
    private Transformable transformable;

    /**
     * Create feature.
     * <p>
     * The {@link Services} must provide the following services:
     * </p>
     * <ul>
     * <li>{@link MapTile}</li>
     * </ul>
     * <p>
     * The {@link Featurable} must have:
     * </p>
     * <ul>
     * <li>{@link Transformable}</li>
     * </ul>
     * <p>
     * The {@link Configurer} can provide a valid {@link ExtractableConfig}.
     * </p>
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public ExtractableModel(Services services, Setup setup)
    {
        super(services, setup);

        if (setup.hasNode(ExtractableConfig.NODE_EXTRACTABLE))
        {
            final ExtractableConfig config = ExtractableConfig.imports(setup);
            type = config.getType();
            resources.setMax(config.getQuantity());
            resources.fill();
        }
    }

    /*
     * Extractable
     */

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        transformable = provider.getFeature(Transformable.class);
    }

    @Override
    public int extractResource(int quantity)
    {
        return resources.decrease(quantity);
    }

    @Override
    public void setResourcesQuantity(int quantity)
    {
        resources.set(quantity);
    }

    @Override
    public void setResourcesType(String type)
    {
        this.type = type;
    }

    @Override
    public int getResourceQuantity()
    {
        return resources.getCurrent();
    }

    @Override
    public String getResourceType()
    {
        return type;
    }

    @Override
    public int getInTileX()
    {
        if (map.isCreated())
        {
            return map.getInTileX(transformable);
        }
        return 0;
    }

    @Override
    public int getInTileY()
    {
        if (map.isCreated())
        {
            return map.getInTileY(transformable);
        }
        return 0;
    }

    @Override
    public int getInTileWidth()
    {
        if (map.isCreated())
        {
            return (int) Math.floor(transformable.getWidth() / (double) map.getTileWidth());
        }
        return 0;
    }

    @Override
    public int getInTileHeight()
    {
        if (map.isCreated())
        {
            return (int) Math.floor(transformable.getHeight() / (double) map.getTileHeight());
        }
        return 0;
    }
}
