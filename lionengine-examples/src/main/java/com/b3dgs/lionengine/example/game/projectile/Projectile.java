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
package com.b3dgs.lionengine.example.game.projectile;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.Sprite;
import com.b3dgs.lionengine.game.CameraGame;
import com.b3dgs.lionengine.game.CollisionData;
import com.b3dgs.lionengine.game.SetupSurfaceGame;
import com.b3dgs.lionengine.game.projectile.ProjectileGame;

/**
 * Projectile base implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
abstract class Projectile
        extends ProjectileGame<Entity, Entity>
{
    /** Projectile surface. */
    private final Sprite sprite;

    /**
     * Constructor.
     * 
     * @param setup The setup reference.
     */
    Projectile(SetupSurfaceGame setup)
    {
        super(setup);
        final int width = getDataInteger("width", "size");
        final int height = getDataInteger("height", "size");
        sprite = Drawable.loadSprite(setup.surface);
        setSize(width, height);
        setCollision(new CollisionData(getWidth(), -getHeight() / 2, 1, 1, false));
    }

    /*
     * ProjectileGame
     */

    @Override
    public void render(Graphic g, CameraGame camera)
    {
        sprite.render(g, camera.getViewpointX(getLocationIntX()), camera.getViewpointY(getLocationIntY()));
    }

    @Override
    public void onHit(Entity entity, int damages)
    {
        destroy();
    }

    @Override
    protected void updateMovement(double extrp, double vecX, double vecY)
    {
        moveLocation(extrp, vecX, vecY);
    }
}
