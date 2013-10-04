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
package com.b3dgs.lionengine.example.d_rts.d_ability;

import java.util.Set;

import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.example.d_rts.d_ability.entity.Entity;
import com.b3dgs.lionengine.game.Bar;
import com.b3dgs.lionengine.game.TextGame;
import com.b3dgs.lionengine.game.rts.CameraRts;
import com.b3dgs.lionengine.game.rts.CursorRts;
import com.b3dgs.lionengine.game.rts.entity.HandlerEntityRts;

/**
 * Handler implementation, containing all entities.
 */
public final class HandlerEntity
        extends HandlerEntityRts<ResourceType, Tile, Entity, ControlPanel>
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
    HandlerEntity(CameraRts camera, CursorRts cursor, ControlPanel controlPanel, Map map, TextGame text)
    {
        super(camera, cursor, controlPanel, map);
        barProgress = new Bar(0, 0);
        barProgress.setBorderSize(1, 1);
    }

    /*
     * HandlerEntityRts
     */

    @Override
    protected void updatingEntity(Entity entity, CursorRts cursor, CameraRts camera)
    {
        // Nothing to do
    }

    @Override
    protected void renderingEntity(Graphic g, Entity entity, CameraRts camera, CursorRts cursor)
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
    protected void notifyUpdatedSelection(Set<Entity> selection)
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
