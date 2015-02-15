/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.example.game.strategy.controlpanel;

import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.game.TextGame;
import com.b3dgs.lionengine.game.strategy.CameraStrategy;
import com.b3dgs.lionengine.game.strategy.CursorStrategy;
import com.b3dgs.lionengine.game.strategy.entity.HandlerEntityStrategy;

/**
 * Handler implementation, containing all entities.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see com.b3dgs.lionengine.example.game.factory
 */
final class HandlerEntity
        extends HandlerEntityStrategy<ResourceType, Tile, Entity>
{
    /** Text reference. */
    private final TextGame text;

    /**
     * Constructor.
     * 
     * @param camera The camera viewpoint.
     * @param cursor The cursor reference (used for selection).
     * @param controlPanel The panel reference.
     * @param map The map reference.
     * @param text The game text reference.
     */
    HandlerEntity(CameraStrategy camera, CursorStrategy cursor, ControlPanel controlPanel, Map map, TextGame text)
    {
        super(camera, cursor, map);
        this.text = text;
    }

    /*
     * HandlerEntityStrategy
     */

    @Override
    protected void updatingEntity(Entity entity, CursorStrategy cursor, CameraStrategy camera)
    {
        // Nothing to do
    }

    @Override
    protected void renderingEntity(Graphic g, Entity entity, CameraStrategy camera, CursorStrategy cursor)
    {
        if (cursor.getClick() == 0 && entity.isOver() || entity.isSelected())
        {
            super.renderingEntity(g, entity, camera, cursor);
            text.draw(g, entity.getLocationIntX() + 18, entity.getLocationIntY() + 16, entity.getClass()
                    .getSimpleName());
        }
        if (entity.isSelected())
        {
            text.draw(g, entity.getLocationIntX() + 18, entity.getLocationIntY() + 8, "Life: " + entity.getLife());
        }
    }

    @Override
    protected ColorRgba getEntityColorOver(Entity entity)
    {
        return ColorRgba.GRAY;
    }

    @Override
    protected ColorRgba getEntityColorSelection(Entity entity)
    {
        return ColorRgba.GREEN;
    }
}
