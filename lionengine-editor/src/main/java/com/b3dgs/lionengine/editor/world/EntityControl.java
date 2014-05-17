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
package com.b3dgs.lionengine.editor.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.CameraGame;
import com.b3dgs.lionengine.game.FactoryObjectGame;
import com.b3dgs.lionengine.game.entity.EntityGame;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.geom.Geom;
import com.b3dgs.lionengine.geom.Rectangle;

/**
 * Allows to control the entity on the editor with the mouse.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class EntityControl
{
    /** World model. */
    private final WorldViewModel model = WorldViewModel.INSTANCE;
    /** Handler entity. */
    private final HandlerEntity handlerEntity;
    /** Mouse over entity flag. */
    private final Map<EntityGame, Boolean> entitiesOver;
    /** Mouse selection entity flag. */
    private final Map<EntityGame, Boolean> entitiesSelection;
    /** Moving offset x. */
    private int movingOffsetX;
    /** Moving offset y. */
    private int movingOffsetY;
    /** Moving entity flag. */
    private boolean dragging;

    /**
     * Constructor.
     * 
     * @param handlerEntity The handler entity reference.
     */
    public EntityControl(HandlerEntity handlerEntity)
    {
        this.handlerEntity = handlerEntity;
        entitiesOver = new HashMap<>();
        entitiesSelection = new HashMap<>();
    }

    /**
     * Update the mouse over entity flag.
     * 
     * @param mx The mouse horizontal location.
     * @param my The mouse vertical location.
     */
    public void updateMouseOver(int mx, int my)
    {
        resetMouseOver();
        final EntityGame entity = getEntity(mx, my);
        if (entity != null)
        {
            setMouseOver(entity, true);
        }
    }

    /**
     * Update the dragging mouse value.
     * 
     * @param oldMx The mouse old horizontal location.
     * @param oldMy The mouse old vertical location.
     * @param mx The mouse horizontal location.
     * @param my The mouse vertical location.
     */
    public void updateDragging(int oldMx, int oldMy, int mx, int my)
    {
        final MapTile<?, ?> map = model.getMap();
        if (!dragging)
        {
            final int th = map.getTileHeight();
            movingOffsetX = UtilMath.getRounded(my, map.getTileWidth()) - mx;
            movingOffsetY = my - UtilMath.getRounded(my, th) - th;
            dragging = true;
        }
        final CameraGame camera = model.getCamera();
        final int th = map.getTileHeight();
        final int areaY = UtilMath.getRounded(camera.getViewHeight(), th);
        final int ox = oldMx + camera.getLocationIntX() + getMovingOffsetX();
        final int oy = areaY - oldMy + camera.getLocationIntY() - 1 + getMovingOffsetY();
        final int x = mx + camera.getLocationIntX() + getMovingOffsetX();
        final int y = areaY - my + camera.getLocationIntY() - 1 + getMovingOffsetY();

        for (final EntityGame entity : handlerEntity.list())
        {
            if (isSelected(entity))
            {
                entity.teleport(entity.getLocationIntX() + x - ox, entity.getLocationIntY() + y - oy);
            }
        }
    }

    /**
     * Reset the entities mouse over state.
     */
    public void resetMouseOver()
    {
        entitiesOver.clear();
    }

    /**
     * Stop the dragging.
     */
    public void stopDragging()
    {
        dragging = false;
    }

    /**
     * Add a new entity at the mouse location.
     * 
     * @param mx The mouse horizontal location.
     * @param my The mouse vertical location.
     */
    public void addEntity(int mx, int my)
    {
        final Class<? extends EntityGame> type = model.getSelectedEntity();
        if (type != null)
        {
            final MapTile<?, ?> map = model.getMap();
            final CameraGame camera = model.getCamera();
            final int tw = map.getTileWidth();
            final int th = map.getTileHeight();
            final int h = UtilMath.getRounded(camera.getViewHeight(), th) - map.getTileHeight();
            final int x = camera.getLocationIntX() + UtilMath.getRounded(mx, tw);
            final int y = camera.getLocationIntY() - UtilMath.getRounded(my, th) + h;

            final FactoryObjectGame<?, ?> factoryEntity = model.getFactoryEntity();
            final EntityGame entity = factoryEntity.createUnsafe(type);
            setEntityLocation(entity, x, y, 1);
            handlerEntity.add(entity);
        }
    }

    /**
     * Remove the entity at the specified location if exists.
     * 
     * @param mx The mouse horizontal location.
     * @param my The mouse vertical location.
     */
    public void removeEntity(int mx, int my)
    {
        final EntityGame entity = getEntity(mx, my);
        if (entity != null)
        {
            entity.destroy();
        }
    }

    /**
     * Select all entities inside the selection area.
     * 
     * @param selectionArea The selection area.
     */
    public void selectEntities(Rectangle selectionArea)
    {
        final MapTile<?, ?> map = model.getMap();
        final CameraGame camera = model.getCamera();

        for (final EntityGame entity : handlerEntity.list())
        {
            final int th = map.getTileHeight();
            final int height = camera.getViewHeight();
            final int offy = height - UtilMath.getRounded(height, th);
            final int sx = UtilMath.getRounded((int) selectionArea.getMinX(), map.getTileWidth());
            final int sy = UtilMath.getRounded(height - (int) selectionArea.getMinY() - offy, th);
            final int ex = UtilMath.getRounded((int) selectionArea.getMaxX(), map.getTileWidth());
            final int ey = UtilMath.getRounded(height - (int) selectionArea.getMaxY() - offy, th);

            if (hitEntity(entity, sx, sy, ex, ey))
            {
                setEntitySelection(entity, true);
            }
        }
    }

    /**
     * Unselect entities.
     */
    public void unSelectEntities()
    {
        for (final EntityGame entity : handlerEntity.list())
        {
            setEntitySelection(entity, false);
        }
    }

    /**
     * Set the mouse over state the the specified entity.
     * 
     * @param entity The entity reference.
     * @param over <code>true</code> if mouse if over, <code>false</code> else.
     */
    public void setMouseOver(EntityGame entity, boolean over)
    {
        entitiesOver.put(entity, Boolean.valueOf(over));
    }

    /**
     * Set the entity selected state.
     * 
     * @param entity The entity reference.
     * @param selected <code>true</code> if selected, <code>false</code> else.
     */
    public void setEntitySelection(EntityGame entity, boolean selected)
    {
        entitiesSelection.put(entity, Boolean.valueOf(selected));
    }

    /**
     * Set the entity location.
     * 
     * @param entity The entity reference.
     * @param x The horizontal location.
     * @param y The vertical location.
     * @param side 1 for place, -1 for move.
     */
    public void setEntityLocation(EntityGame entity, int x, int y, int side)
    {
        final MapTile<?, ?> map = model.getMap();
        final int tw = map.getTileWidth();
        final int th = map.getTileHeight();
        entity.teleport(UtilMath.getRounded(x + (side == 1 ? 0 : 1) * entity.getWidth() / 2 + tw / 2, tw) + side
                * entity.getWidth() / 2, UtilMath.getRounded(y + th / 2, th));
    }

    /**
     * Get the entity at the specified mouse location.
     * 
     * @param mx The mouse horizontal location.
     * @param my The mouse vertical location.
     * @return The entity reference, <code>null</code> if none.
     */
    public EntityGame getEntity(int mx, int my)
    {
        final MapTile<?, ?> map = model.getMap();
        final CameraGame camera = model.getCamera();
        final int x = UtilMath.getRounded(mx, map.getTileWidth());
        final int y = UtilMath.getRounded(camera.getViewHeight() - my - 1, map.getTileHeight());
        for (final EntityGame entity : handlerEntity.list())
        {
            if (hitEntity(entity, x, y, x + map.getTileWidth(), y + map.getTileHeight()))
            {
                return entity;
            }
        }

        return null;
    }

    /**
     * Get the list of selected entities.
     * 
     * @return The selected entities.
     */
    public List<EntityGame> getSelectedEnties()
    {
        final List<EntityGame> list = new ArrayList<>(0);
        for (final EntityGame entity : handlerEntity.list())
        {
            if (isSelected(entity))
            {
                list.add(entity);
            }
        }
        return list;
    }

    /**
     * Get the horizontal moving offset.
     * 
     * @return The horizontal moving offset.
     */
    public int getMovingOffsetX()
    {
        return movingOffsetX;
    }

    /**
     * Get the vertical moving offset.
     * 
     * @return The vertical moving offset.
     */
    public int getMovingOffsetY()
    {
        return movingOffsetY;
    }

    /**
     * Check the mouse over entity flag.
     * 
     * @param entity The entity to check.
     * @return <code>true</code> if over, <code>false</code> else.
     */
    public boolean isOver(EntityGame entity)
    {
        if (entitiesOver.containsKey(entity))
        {
            return entitiesOver.get(entity).booleanValue();
        }
        return false;
    }

    /**
     * Check the mouse entity selection flag.
     * 
     * @param entity The entity to check.
     * @return <code>true</code> if selected, <code>false</code> else.
     */
    public boolean isSelected(EntityGame entity)
    {
        if (entitiesSelection.containsKey(entity))
        {
            return entitiesSelection.get(entity).booleanValue();
        }
        return false;
    }

    /**
     * Check if mouse if dragging.
     * 
     * @return <code>true</code> if dragging, <code>false</code> else.
     */
    public boolean isDragging()
    {
        return dragging;
    }

    /**
     * Check if entity is hit.
     * 
     * @param entity The entity to check.
     * @param x1 First point x.
     * @param y1 First point y.
     * @param x2 Second point x.
     * @param y2 Second point y.
     * @return <code>true</code> if hit, <code>false</code> else.
     */
    private boolean hitEntity(EntityGame entity, int x1, int y1, int x2, int y2)
    {
        final MapTile<?, ?> map = model.getMap();
        final CameraGame camera = model.getCamera();
        if (entity != null)
        {
            final int x = UtilMath.getRounded(entity.getLocationIntX() - entity.getWidth() / 2, map.getTileWidth())
                    - camera.getLocationIntX();
            final int y = UtilMath.getRounded(entity.getLocationIntY(), map.getTileHeight()) - camera.getLocationIntY();
            final Rectangle r1 = Geom.createRectangle(x1, y1, x2 - x1, y2 - y1);
            final Rectangle r2 = Geom.createRectangle(x, y, entity.getWidth(), entity.getHeight());
            if (r1.intersects(r2))
            {
                return true;
            }
        }
        return false;
    }
}
