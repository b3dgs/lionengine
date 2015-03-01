/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.object.ObjectGame;
import com.b3dgs.lionengine.game.object.Services;

/**
 * Fovable model implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class FovableModel
        extends TraitModel
        implements Fovable
{
    /** Transformable model. */
    private final Transformable transformable;
    /** Map tile reference. */
    private final MapTile map;
    /** Field of view in tile value. */
    private int fov;

    /**
     * Create a fovable model.
     * <p>
     * The owner must have the following {@link Trait}:
     * </p>
     * <ul>
     * <li>{@link Transformable}</li>
     * </ul>
     * The {@link Services} must provide the following services:
     * </p>
     * <ul>
     * <li>{@link MapTile}</li>
     * </ul>
     * 
     * @param owner The owner reference.
     * @param services The services reference.
     * @throws LionEngineException If missing {@link Trait} or {@link Services}.
     */
    public FovableModel(ObjectGame owner, Services services) throws LionEngineException
    {
        super(owner);
        transformable = owner.getTrait(Transformable.class);
        map = services.get(MapTile.class);
    }

    /*
     * Fovable
     */

    @Override
    public void setFov(int fov)
    {
        this.fov = fov;
    }

    @Override
    public int getInTileX()
    {
        return (int) (transformable.getX() / map.getTileWidth());
    }

    @Override
    public int getInTileY()
    {
        return (int) (transformable.getY() / map.getTileHeight());
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
