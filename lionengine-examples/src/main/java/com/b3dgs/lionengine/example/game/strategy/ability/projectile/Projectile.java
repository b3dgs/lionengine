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
package com.b3dgs.lionengine.example.game.strategy.ability.projectile;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.SpriteTiled;
import com.b3dgs.lionengine.example.game.strategy.ability.entity.Entity;
import com.b3dgs.lionengine.example.game.strategy.ability.weapon.Weapon;
import com.b3dgs.lionengine.game.CameraGame;
import com.b3dgs.lionengine.game.SetupSurfaceGame;
import com.b3dgs.lionengine.game.projectile.ProjectileGame;

/**
 * Projectile implementation base.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see com.b3dgs.lionengine.example.game.projectile
 */
public abstract class Projectile
        extends ProjectileGame<Entity, Weapon>
{
    /** Surface. */
    private final SpriteTiled sprite;
    /** Frame. */
    private int frame;

    /**
     * Constructor.
     * 
     * @param setup The entity setup.
     */
    protected Projectile(SetupSurfaceGame setup)
    {
        super(setup);
        sprite = Drawable.loadSpriteTiled(setup.surface, getWidth(), getHeight());
        sprite.load(false);
    }

    /**
     * The projectile frame to set.
     * 
     * @param frame The frame.
     */
    public void setFrame(int frame)
    {
        this.frame = frame;
    }

    /*
     * ProjectileGame
     */

    @Override
    public void render(Graphic g, CameraGame camera)
    {
        sprite.render(g, frame, camera.getViewpointX(getLocationIntX()), camera.getViewpointY(getLocationIntY()));
    }

    @Override
    protected void updateMovement(double extrp, double vecX, double vecY)
    {
        // Apply a linear movement to the projectile with its vector
        moveLocation(extrp, vecX, vecY);
    }
}
