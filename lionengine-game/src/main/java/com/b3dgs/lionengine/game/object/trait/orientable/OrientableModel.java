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
package com.b3dgs.lionengine.game.object.trait.orientable;

import com.b3dgs.lionengine.game.Orientation;
import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.object.ObjectGame;
import com.b3dgs.lionengine.game.object.trait.TraitModel;
import com.b3dgs.lionengine.game.object.trait.transformable.Transformable;
import com.b3dgs.lionengine.game.tile.Tiled;

/**
 * Orientable model implementation.
 * <p>
 * The {@link ObjectGame} owner must have the following {@link com.b3dgs.lionengine.game.object.trait.Trait}:
 * </p>
 * <ul>
 * <li>{@link Transformable}</li>
 * </ul>
 * <p>
 * The {@link Services} must provide the following services:
 * </p>
 * <ul>
 * <li>{@link MapTile}</li>
 * </ul>
 */
public class OrientableModel extends TraitModel implements Orientable
{
    /** Map reference. */
    private MapTile map;
    /** Localizable reference. */
    private Transformable transformable;
    /** Current orientation. */
    private Orientation orientation;

    /**
     * Create an orientable model.
     */
    public OrientableModel()
    {
        super();
        orientation = Orientation.NORTH;
    }

    /*
     * Orientable
     */

    @Override
    public void prepare(ObjectGame owner, Services services)
    {
        transformable = owner.getTrait(Transformable.class);
        map = services.get(MapTile.class);
    }

    @Override
    public void pointTo(int dtx, int dty)
    {
        final int tx = map.getInTileX(transformable);
        final int ty = map.getInTileY(transformable);
        final Orientation next = Orientation.get(tx, ty, dtx, dty);
        if (next != null)
        {
            orientation = next;
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
