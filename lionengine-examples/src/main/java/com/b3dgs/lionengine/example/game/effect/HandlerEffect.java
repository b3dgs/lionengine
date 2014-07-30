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
package com.b3dgs.lionengine.example.game.effect;

import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.game.CameraGame;
import com.b3dgs.lionengine.game.HandlerGame;

/**
 * Handler effect implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see com.b3dgs.lionengine.example.game.handler
 */
final class HandlerEffect
        extends HandlerGame<Effect>
{
    /** Camera reference. */
    private final CameraGame camera;

    /**
     * Constructor.
     * 
     * @param camera The camera reference.
     */
    public HandlerEffect(CameraGame camera)
    {
        super();
        this.camera = camera;
    }

    @Override
    protected void update(double extrp, Effect handlable)
    {
        handlable.update(extrp);
    }

    @Override
    protected void render(Graphic g, Effect handlable)
    {
        if (camera.isVisible(handlable))
        {
            handlable.render(g, camera);
        }
    }
}
