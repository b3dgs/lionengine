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
package com.b3dgs.lionengine.game.effect;

import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.game.CameraGame;
import com.b3dgs.lionengine.game.HandlerObjectGame;

/**
 * Handle effects. Improve the default handler by not rendering effects that are outside the camera view.
 * 
 * @param <E> The effect type used.
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class HandlerEffectGame<E extends EffectGame>
        extends HandlerObjectGame<E>
{
    /** Camera reference. */
    private final CameraGame camera;

    /**
     * Constructor.
     * 
     * @param camera The camera reference.
     */
    public HandlerEffectGame(CameraGame camera)
    {
        super();
        this.camera = camera;
    }

    /*
     * HandlerGame
     */

    @Override
    protected void update(double extrp, E effect)
    {
        effect.update(extrp);
    }

    @Override
    protected void render(Graphic g, E effect)
    {
        if (camera.isVisible(effect))
        {
            effect.render(g, camera);
        }
    }
}
