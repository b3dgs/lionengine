/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import com.b3dgs.lionengine.drawable.SpriteTiled;
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
    private final SpriteTiled sprite;
    /** Frame number. */
    private final int frame;

    /**
     * Constructor.
     * 
     * @param setup The setup reference.
     * @param frame The frame number.
     */
    Projectile(SetupSurfaceGame setup, int frame)
    {
        super(setup);
        this.frame = frame;
        final int tileWidth = getDataInteger("width", "tiles");
        final int tileHeight = getDataInteger("height", "tiles");
        sprite = Drawable.loadSpriteTiled(setup.surface, tileWidth, tileHeight);
        setCollision(new CollisionData(getWidth(), -getHeight() / 2, 1, 1, false));
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
