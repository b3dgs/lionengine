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
package com.b3dgs.lionengine.game.trait.orientable;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.Orientation;
import com.b3dgs.lionengine.game.Tiled;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.object.ObjectGame;
import com.b3dgs.lionengine.game.object.Services;
import com.b3dgs.lionengine.game.trait.Trait;
import com.b3dgs.lionengine.game.trait.TraitModel;
import com.b3dgs.lionengine.game.trait.transformable.Transformable;

/**
 * Orientable model implementation.
 * <p>
 * The {@link ObjectGame} owner must have the following {@link Trait}:
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
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class OrientableModel
        extends TraitModel
        implements Orientable
{
    /** Map reference. */
    private final MapTile map;
    /** Localizable reference. */
    private Transformable transformable;
    /** Current orientation. */
    private Orientation orientation;

    /**
     * Create an orientable model.
     * 
     * @param owner The owner reference.
     * @param services The services reference.
     * @throws LionEngineException If missing {@link Services}.
     */
    public OrientableModel(ObjectGame owner, Services services) throws LionEngineException
    {
        super(owner, services);
        map = services.get(MapTile.class);
        orientation = Orientation.NORTH;
    }

    /*
     * Orientable
     */

    @Override
    public void prepare(Services services)
    {
        transformable = owner.getTrait(Transformable.class);
    }

    @Override
    public void pointTo(int dtx, int dty)
    {
        final int tx = (int) transformable.getX() / map.getTileWidth();
        final int ty = (int) transformable.getY() / map.getTileHeight();
        if (ty < dty)
        {
            if (tx < dtx)
            {
                orientation = Orientation.NORTH_EAST;
            }
            else if (tx > dtx)
            {
                orientation = Orientation.NORTH_WEST;
            }
            else
            {
                orientation = Orientation.NORTH;
            }
        }
        else if (ty > dty)
        {
            if (tx > dtx)
            {
                orientation = Orientation.SOUTH_WEST;
            }
            else if (tx < dtx)
            {
                orientation = Orientation.SOUTH_EAST;
            }
            else
            {
                orientation = Orientation.SOUTH;
            }
        }
        else
        {
            if (tx < dtx)
            {
                orientation = Orientation.EAST;
            }
            else if (tx > dtx)
            {
                orientation = Orientation.WEST;
            }
        }
    }

    @Override
    public void pointTo(Tiled tiled)
    {
        pointTo(tiled.getInTileX(), tiled.getInTileY());
    }

    @Override
    public void setOrientation(Orientation orientation)
    {
        this.orientation = orientation;
    }

    @Override
    public Orientation getOrientation()
    {
        return orientation;
    }
}
