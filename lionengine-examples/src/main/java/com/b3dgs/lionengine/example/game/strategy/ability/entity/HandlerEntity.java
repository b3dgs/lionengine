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
package com.b3dgs.lionengine.example.game.strategy.ability.entity;

import java.util.Collection;

import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.example.game.strategy.ability.ControlPanel;
import com.b3dgs.lionengine.example.game.strategy.ability.ResourceType;
import com.b3dgs.lionengine.example.game.strategy.ability.map.Map;
import com.b3dgs.lionengine.example.game.strategy.ability.map.Tile;
import com.b3dgs.lionengine.game.Bar;
import com.b3dgs.lionengine.game.TextGame;
import com.b3dgs.lionengine.game.strategy.CameraStrategy;
import com.b3dgs.lionengine.game.strategy.CursorStrategy;
import com.b3dgs.lionengine.game.strategy.entity.HandlerEntityStrategy;

/**
 * Handler implementation, containing all entities.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class HandlerEntity
        extends HandlerEntityStrategy<ResourceType, Tile, Entity, ControlPanel>
{
    /** Entity progress bar. */
    private final Bar barProgress;

    /**
     * Constructor.
     * 
     * @param camera The camera viewpoint.
     * @param cursor The cursor reference (used for selection).
     * @param controlPanel The panel reference.
     * @param map The map reference.
     * @param text The text reference.
     */
    public HandlerEntity(CameraStrategy camera, CursorStrategy cursor, ControlPanel controlPanel, Map map, TextGame text)
    {
        super(camera, cursor, controlPanel, map);
        barProgress = new Bar(0, 0);
        barProgress.setBorderSize(1, 1);
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
        }
        if (entity.isSelected())
        {
            final int x = camera.getViewpointX(entity.getLocationIntX());
            final int y = camera.getViewpointY(entity.getLocationIntY() + 4 + entity.getHeight());

            barProgress.setLocation(x, y);
            barProgress.setMaximumSize(entity.getWidth(), 4);
            barProgress.setWidthPercent(entity.life.getPercent());
            barProgress.setColorBackground(ColorRgba.GRAY);
            barProgress.setColorForeground(ColorRgba.RED);
            barProgress.render(g);
        }
    }

    @Override
    protected void notifyUpdatedSelection(Collection<Entity> selection)
    {
        // Nothing to do
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
