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
package com.b3dgs.lionengine.example.game.collision;

import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.core.drawable.Drawable;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.graphic.Sprite;

/**
 * Player model implementation.
 */
public class PlayerModel
{
    private final Force movement = new Force();
    private final Force jump = new Force();
    private final Sprite surface;

    /**
     * Create model.
     * 
     * @param setup The setup reference.
     */
    public PlayerModel(Setup setup)
    {
        surface = Drawable.loadSprite(setup.getSurface());
        surface.setOrigin(Origin.CENTER_BOTTOM);

        jump.setVelocity(0.1);
        jump.setDestination(0.0, 0.0);
    }

    /**
     * Get the movement force.
     * 
     * @return The movement force.
     */
    public Force getMovement()
    {
        return movement;
    }

    /**
     * Get the jump force.
     * 
     * @return THe jump force.
     */
    public Force getJump()
    {
        return jump;
    }

    /**
     * Get the surface.
     * 
     * @return The surface.
     */
    public Sprite getSurface()
    {
        return surface;
    }
}
