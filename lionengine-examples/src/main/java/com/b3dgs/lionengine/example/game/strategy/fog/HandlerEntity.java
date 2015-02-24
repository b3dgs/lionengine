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
package com.b3dgs.lionengine.example.game.strategy.fog;

import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.game.object.Handler;
import com.b3dgs.lionengine.game.strategy.CameraStrategy;

/**
 * Handler implementation, containing all entities.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see com.b3dgs.lionengine.example.game.factory
 */
final class HandlerEntity
        extends Handler<Entity>
{
    /** Camera reference. */
    private final CameraStrategy camera;

    /**
     * Constructor.
     * 
     * @param camera The camera reference.
     */
    HandlerEntity(CameraStrategy camera)
    {
        super();
        this.camera = camera;
    }

    /*
     * HandlerObjectGame
     */

    @Override
    protected void update(double extrp, Entity entity)
    {
        entity.update(extrp);
    }

    @Override
    protected void render(Graphic g, Viewer viewer, Entity entity)
    {
        entity.render(g, camera);
    }
}
