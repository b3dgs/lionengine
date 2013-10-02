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
package com.b3dgs.lionengine.example.e_shmup.c_tyrian.entity;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.SpriteTiled;
import com.b3dgs.lionengine.game.CameraGame;
import com.b3dgs.lionengine.game.CollisionData;
import com.b3dgs.lionengine.game.SetupSurfaceGame;
import com.b3dgs.lionengine.game.entity.EntityGame;

/**
 * Entity base implementation.
 */
public class Entity
        extends EntityGame
{
    /** Entity surface. */
    private final SpriteTiled sprite;
    /** Tile offset. */
    private int tileOffset;

    /**
     * Constructor.
     * 
     * @param setup The setup reference.
     */
    public Entity(SetupSurfaceGame setup)
    {
        super(setup);
        final int width = setup.configurable.getDataInteger("width", "size");
        final int height = setup.configurable.getDataInteger("height", "size");
        sprite = Drawable.loadSpriteTiled(setup.surface, width, height);
        setSize(width, height);
        setCollision(new CollisionData(width / 2, -height, width, height, false));
    }

    /**
     * Render the entity.
     * 
     * @param g The graphic output.
     * @param camera The camera reference.
     */
    public void render(Graphic g, CameraGame camera)
    {
        final int x = camera.getViewpointX(getLocationIntX()) + getLocationOffsetX();
        final int y = camera.getViewpointY(getLocationIntY()) - getLocationOffsetY();
        sprite.render(g, tileOffset, x, y);
    }

    /**
     * Set the tile offset.
     * 
     * @param offset The tile offset.
     */
    protected void setTileOffset(int offset)
    {
        tileOffset = offset;
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
