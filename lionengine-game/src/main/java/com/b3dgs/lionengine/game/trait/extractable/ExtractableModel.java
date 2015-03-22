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
package com.b3dgs.lionengine.game.trait.extractable;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.Alterable;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.object.ObjectGame;
import com.b3dgs.lionengine.game.object.Services;
import com.b3dgs.lionengine.game.trait.TraitModel;
import com.b3dgs.lionengine.game.trait.transformable.Transformable;

/**
 * Extractable model implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class ExtractableModel
        extends TraitModel
        implements Extractable
{
    /** Map reference. */
    private final MapTile map;
    /** Transformable model. */
    private Transformable transformable;
    /** Resources count. */
    private final Alterable resources;
    /** Resource type. */
    private Enum<?> type;

    /**
     * Create an extractable model.
     * 
     * @param owner The owner reference.
     * @param services The services reference.
     * @throws LionEngineException If services are <code>null</code>.
     */
    public ExtractableModel(ObjectGame owner, Services services)
    {
        super(owner, services);
        map = services.get(MapTile.class);
        resources = new Alterable(0);
    }

    /*
     * Extractable
     */

    @Override
    public void prepare(Services services)
    {
        transformable = owner.getTrait(Transformable.class);
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
    public void setResourcesType(Enum<?> type)
    {
        this.type = type;
    }

    @Override
    public int getResourceQuantity()
    {
        return resources.getCurrent();
    }

    @Override
    public Enum<?> getResourceType()
    {
        return type;
    }

    @Override
    public int getInTileX()
    {
        return (int) transformable.getX() / map.getTileWidth();
    }

    @Override
    public int getInTileY()
    {
        return (int) transformable.getY() / map.getTileHeight();
    }

    @Override
    public int getInTileWidth()
    {
        return transformable.getWidth() / map.getTileWidth();
    }

    @Override
    public int getInTileHeight()
    {
        return transformable.getHeight() / map.getInTileHeight();
    }
}
