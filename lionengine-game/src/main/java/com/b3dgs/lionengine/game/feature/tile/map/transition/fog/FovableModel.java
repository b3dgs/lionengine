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
package com.b3dgs.lionengine.game.feature.tile.map.transition.fog;

import com.b3dgs.lionengine.game.Featurable;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;

/**
 * Fovable model implementation.
 */
public class FovableModel extends FeatureModel implements Fovable
{
    /** Map tile reference. */
    private final MapTile map;
    /** Transformable model. */
    private Transformable transformable;
    /** Field of view in tile value. */
    private int fov;

    /**
     * Create a fovable model.
     * <p>
     * The {@link Services} must provide:
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
     * 
     * @param services The services reference.
     */
    public FovableModel(Services services)
    {
        super();

        map = services.get(MapTile.class);
    }

    /*
     * Fovable
     */

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        transformable = provider.getFeature(Transformable.class);
    }

    @Override
    public void setFov(int fov)
    {
        this.fov = fov;
    }

    @Override
    public int getInTileX()
    {
        return map.getInTileX(transformable);
    }

    @Override
    public int getInTileY()
    {
        return map.getInTileY(transformable);
    }

    @Override
    public int getInTileWidth()
    {
        return transformable.getWidth() / map.getTileWidth();
    }

    @Override
    public int getInTileHeight()
    {
        return transformable.getHeight() / map.getTileHeight();
    }

    @Override
    public int getInTileFov()
    {
        return fov;
    }
}
