/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.editor.world;

import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.game.CameraGame;
import com.b3dgs.lionengine.game.entity.EntityGame;
import com.b3dgs.lionengine.game.entity.HandlerEntityGame;

/**
 * Handler entity implementation. Just used as a simple entity rendering.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class HandlerEntity
        extends HandlerEntityGame<EntityGame>
{
    /** Camera. */
    private final CameraGame camera;

    /**
     * Constructor.
     * 
     * @param camera The camera reference.
     */
    public HandlerEntity(CameraGame camera)
    {
        this.camera = camera;
    }

    /*
     * HandlerEntityGame
     */

    @Override
    protected void update(double extrp, EntityGame entity)
    {
        // Nothing to do
    }

    @Override
    protected void render(Graphic g, EntityGame entity)
    {
        entity.render(g, camera);
    }
}
