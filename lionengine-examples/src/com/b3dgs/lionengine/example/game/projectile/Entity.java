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
import com.b3dgs.lionengine.core.UtilityMedia;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.SpriteTiled;
import com.b3dgs.lionengine.game.CameraGame;
import com.b3dgs.lionengine.game.CollisionData;
import com.b3dgs.lionengine.game.SetupGame;
import com.b3dgs.lionengine.game.entity.EntityGame;

/**
 * Entity game implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
final class Entity
        extends EntityGame
{
    /** Surface. */
    private final SpriteTiled sprite;

    /**
     * Constructor.
     */
    Entity()
    {
        super(new SetupGame(UtilityMedia.get("entity.xml")));
        sprite = Drawable.loadSpriteTiled(UtilityMedia.get("entity.png"), 24, 28);
        sprite.load(false);
        setSize(24, 28);
        setCollision(new CollisionData(getWidth() / 2, -getHeight(), getWidth(), getHeight(), false));
    }

    /**
     * Render the entity.
     * 
     * @param g The graphic output.
     * @param camera The camera reference.
     */
    public void render(Graphic g, CameraGame camera)
    {
        sprite.render(g, 2, camera.getViewpointX(getLocationIntX()), camera.getViewpointY(getLocationIntY()));
    }

    /*
     * EntityGame
     */

    @Override
    public void update(double extrp)
    {
        updateCollision();
    }
}
