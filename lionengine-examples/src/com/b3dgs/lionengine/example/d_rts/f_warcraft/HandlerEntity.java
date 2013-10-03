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
package com.b3dgs.lionengine.example.d_rts.f_warcraft;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.b3dgs.lionengine.core.ColorRgba;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.entity.Building;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.entity.Entity;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.entity.Unit;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.type.TypeCursor;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.type.TypeResource;
import com.b3dgs.lionengine.game.rts.CameraRts;
import com.b3dgs.lionengine.game.rts.CursorRts;
import com.b3dgs.lionengine.game.rts.entity.HandlerEntityRts;

/**
 * Handler implementation, containing all entities.
 */
public final class HandlerEntity
        extends HandlerEntityRts<TypeResource, Tile, Entity, ControlPanel>
{
    /** Cursor reference. */
    private final Cursor cursor;
    /** Fog of war reference. */
    private final FogOfWar fogOfWar;
    /** The player. */
    private Player player;

    /**
     * Constructor.
     * 
     * @param camera The camera reference.
     * @param cursor The cursor reference.
     * @param controlPanel The panel reference.
     * @param map The map reference.
     * @param fogOfWar The fog of war reference.
     */
    HandlerEntity(CameraRts camera, Cursor cursor, ControlPanel controlPanel, Map map, FogOfWar fogOfWar)
    {
        super(camera, cursor, controlPanel, map);
        this.cursor = cursor;
        this.fogOfWar = fogOfWar;
    }

    /**
     * Set the player reference for the panel information.
     * 
     * @param player The player reference.
     */
    public void setPlayer(Player player)
    {
        this.player = player;
        setPlayerId(player.id);
    }

    /**
     * Update the population capacity count.
     */
    public void updatePopulation()
    {
        for (final Entity entity : list())
        {
            if (entity.getPlayer() == player)
            {
                if (entity instanceof Unit)
                {
                    player.changePopulation(1);
                }
            }
        }
    }

    /*
     * HandlerEntityRts
     */

    @Override
    public void update(double extrp)
    {
        if (cursor.getType() == TypeCursor.WEN)
        {
            cursor.setType(TypeCursor.POINTER);
        }
        super.update(extrp);
        fogOfWar.update(list());
    }

    @Override
    protected void updatingEntity(Entity entity, CursorRts cursor, CameraRts camera)
    {
        // Adapt cursor
        entity.setSelectable(!fogOfWar.isFogged(entity));
        if (cursor.getClick() == 0 && this.cursor.getType() == TypeCursor.POINTER && entity.isOver()
                && cursor.isOver(entity, camera) && entity.isSelectable())
        {
            this.cursor.setType(TypeCursor.WEN);
        }
    }

    @Override
    protected void renderingEntity(Graphic g, Entity entity, CameraRts camera, CursorRts cursor)
    {
        if (cursor.getClick() == 0 && entity.isOver() && this.cursor.getType() != TypeCursor.BOX
                && !panel.canClick(cursor) || entity.isSelected())
        {
            super.renderingEntity(g, entity, camera, cursor);
        }
    }

    @Override
    protected void notifyUpdatedSelection(Set<Entity> selection)
    {
        boolean hasUnit = false;
        boolean hasBuilding = false;
        boolean hasEnemy = false;
        Entity firstBuilding = null;
        Entity firstEnemy = null;
        final List<Entity> toUnselect = new ArrayList<>(selection.size() / 2);
        for (final Entity entity : selection)
        {
            // Same faction selection
            if (player == entity.getPlayer())
            {
                if (entity instanceof Unit)
                {
                    hasUnit = true;
                }
                // Maximum of 1 selected building
                if (entity instanceof Building)
                {
                    if (hasBuilding || hasUnit)
                    {
                        toUnselect.add(entity);
                    }
                    else
                    {
                        hasBuilding = true;
                        firstBuilding = entity;
                    }
                }
            }
            // Maximum of one other entity faction selected
            else
            {
                if (hasEnemy)
                {
                    toUnselect.add(entity);
                }
                else
                {
                    hasEnemy = true;
                    firstEnemy = entity;
                }
            }
        }
        // Unselect building if there are selected units
        if (hasUnit && firstBuilding != null)
        {
            toUnselect.add(firstBuilding);
        }
        // Unselect enemy if there are selected units or buildings
        if ((hasUnit || hasBuilding) && firstEnemy != null)
        {
            toUnselect.add(firstEnemy);
        }
        for (final Entity entity : toUnselect)
        {
            entity.setSelection(false);
            selection.remove(entity);
        }
        toUnselect.clear();
    }

    @Override
    protected ColorRgba getEntityColorOver(Entity entity)
    {
        return ColorRgba.GRAY;
    }

    @Override
    protected ColorRgba getEntityColorSelection(Entity entity)
    {
        if (entity.getPlayer() == player)
        {
            return ColorRgba.GREEN;
        }
        else if (entity.getPlayer() == null)
        {
            return ColorRgba.GRAY_LIGHT;
        }
        return ColorRgba.RED;
    }
}
