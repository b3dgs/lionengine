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
package com.b3dgs.lionengine.example.game.platform.tile;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.core.Click;
import com.b3dgs.lionengine.core.Mouse;
import com.b3dgs.lionengine.core.UtilityMedia;
import com.b3dgs.lionengine.game.CollisionData;
import com.b3dgs.lionengine.game.SetupSurfaceGame;
import com.b3dgs.lionengine.game.platform.CameraPlatform;
import com.b3dgs.lionengine.game.platform.entity.EntityPlatform;

/**
 * Entity implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see com.b3dgs.lionengine.example.game.entity
 */
final class Entity
        extends EntityPlatform
{
    /** Map reference. */
    private final Map map;
    /** Mouse click x. */
    private int mouseX;
    /** Mouse click y. */
    private int mouseY;
    /** Last tile collision. */
    private Tile tile;

    /**
     * Constructor.
     * 
     * @param map The map reference.
     */
    Entity(Map map)
    {
        super(new SetupSurfaceGame(UtilityMedia.get("mario.xml")));
        this.map = map;
        mouseX = 64;
        mouseY = 180;
        setCollision(new CollisionData(0, 0, 16, 16, false));
    }

    /**
     * Update the mouse.
     * 
     * @param mouse The mouse.
     */
    public void updateMouse(Mouse mouse)
    {
        if (mouse.hasClicked(Click.LEFT))
        {
            mouseX = mouse.getX();
            mouseY = mouse.getY();
        }
    }

    /*
     * EntityPlatform
     */

    @Override
    public void render(Graphic g, CameraPlatform camera)
    {
        super.render(g, camera);
        renderCollision(g, camera);
        if (tile != null)
        {
            tile.renderCollision(g, camera);
        }
    }

    @Override
    protected void handleActions(double extrp)
    {
        setLocation(192, 112);
    }

    @Override
    protected void handleMovements(double extrp)
    {
        moveLocation(extrp, mouseX - 200, -mouseY + 128);
    }

    @Override
    protected void handleCollisions(double extrp)
    {
        updateCollision();
        tile = map.getFirstTileHit(this, TileCollision.COLLISION, false);
    }

    @Override
    protected void handleAnimations(double extrp)
    {
        // Nothing to do
    }
}
