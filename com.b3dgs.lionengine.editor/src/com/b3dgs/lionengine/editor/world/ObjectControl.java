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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.editor.Tools;
import com.b3dgs.lionengine.game.CameraGame;
import com.b3dgs.lionengine.game.FactoryObjectGame;
import com.b3dgs.lionengine.game.ObjectGame;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.geom.Geom;
import com.b3dgs.lionengine.geom.Point;
import com.b3dgs.lionengine.geom.Rectangle;

/**
 * Allows to control the object on the editor with the mouse.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class ObjectControl
{
    /** World model. */
    private final WorldViewModel model = WorldViewModel.INSTANCE;
    /** Handler object. */
    private final HandlerObject handlerObject;
    /** Mouse over object flag. */
    private final Map<ObjectGame, Boolean> objectsOver;
    /** Mouse selection object flag. */
    private final Map<ObjectGame, Boolean> objectsSelection;
    /** Moving offset x. */
    private int movingOffsetX;
    /** Moving offset y. */
    private int movingOffsetY;
    /** Moving object flag. */
    private boolean dragging;

    /**
     * Create the object control from object handler.
     * 
     * @param handlerObject The handler object reference.
     */
    public ObjectControl(HandlerObject handlerObject)
    {
        this.handlerObject = handlerObject;
        objectsOver = new HashMap<>();
        objectsSelection = new HashMap<>();
    }

    /**
     * Update the mouse over object flag.
     * 
     * @param mx The mouse horizontal location.
     * @param my The mouse vertical location.
     */
    public void updateMouseOver(int mx, int my)
    {
        resetMouseOver();
        final ObjectGame object = getObject(mx, my);
        if (object != null)
        {
            setMouseOver(object, true);
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
        final MapTile<?> map = model.getMap();
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

        for (final ObjectGame object : handlerObject.list())
        {
            if (isSelected(object))
            {
                object.teleport(object.getLocationIntX() + x - ox, object.getLocationIntY() + y - oy);
            }
        }
    }

    /**
     * Reset the objects mouse over state.
     */
    public void resetMouseOver()
    {
        objectsOver.clear();
    }

    /**
     * Stop the dragging.
     */
    public void stopDragging()
    {
        dragging = false;
    }

    /**
     * Add a new object at the mouse location.
     * 
     * @param mx The mouse horizontal location.
     * @param my The mouse vertical location.
     */
    public void addEntity(int mx, int my)
    {
        final Media media = model.getSelectedObject();
        if (media != null)
        {
            final MapTile<?> map = model.getMap();
            final CameraGame camera = model.getCamera();
            final Point tile = Tools.getMouseTile(map, camera, mx, my);
            final FactoryObjectGame<?> factoryEntity = model.getFactory();
            final ObjectGame object = factoryEntity.create(media);

            setObjectLocation(object, tile.getX(), tile.getY(), 1);
            handlerObject.add(object);
        }
    }

    /**
     * Remove the object at the specified location if exists.
     * 
     * @param mx The mouse horizontal location.
     * @param my The mouse vertical location.
     */
    public void removeEntity(int mx, int my)
    {
        final ObjectGame object = getObject(mx, my);
        if (object != null)
        {
            object.destroy();
        }
    }

    /**
     * Select all objects inside the selection area.
     * 
     * @param selectionArea The selection area.
     */
    public void selectEntities(Rectangle selectionArea)
    {
        final MapTile<?> map = model.getMap();
        final CameraGame camera = model.getCamera();

        for (final ObjectGame object : handlerObject.list())
        {
            final int th = map.getTileHeight();
            final int height = camera.getViewHeight();
            final int offy = height - UtilMath.getRounded(height, th);
            final int sx = UtilMath.getRounded((int) selectionArea.getMinX(), map.getTileWidth());
            final int sy = UtilMath.getRounded(height - (int) selectionArea.getMinY() - offy, th);
            final int ex = UtilMath.getRounded((int) selectionArea.getMaxX(), map.getTileWidth());
            final int ey = UtilMath.getRounded(height - (int) selectionArea.getMaxY() - offy, th);

            if (hitObject(object, sx, sy, ex, ey))
            {
                setObjectSelection(object, true);
            }
        }
    }

    /**
     * Unselect objects.
     */
    public void unSelectEntities()
    {
        for (final ObjectGame object : handlerObject.list())
        {
            setObjectSelection(object, false);
        }
    }

    /**
     * Set the mouse over state the the specified object.
     * 
     * @param object The object reference.
     * @param over <code>true</code> if mouse if over, <code>false</code> else.
     */
    public void setMouseOver(ObjectGame object, boolean over)
    {
        objectsOver.put(object, Boolean.valueOf(over));
    }

    /**
     * Set the object selected state.
     * 
     * @param object The object reference.
     * @param selected <code>true</code> if selected, <code>false</code> else.
     */
    public void setObjectSelection(ObjectGame object, boolean selected)
    {
        objectsSelection.put(object, Boolean.valueOf(selected));
    }

    /**
     * Set the object location.
     * 
     * @param object The object reference.
     * @param x The horizontal location.
     * @param y The vertical location.
     * @param side 1 for place, -1 for move.
     */
    public void setObjectLocation(ObjectGame object, int x, int y, int side)
    {
        final MapTile<?> map = model.getMap();
        final int tw = map.getTileWidth();
        final int th = map.getTileHeight();
        object.teleport(UtilMath.getRounded(x + (side == 1 ? 0 : 1) * object.getWidth() / 2 + tw / 2, tw) + side
                * object.getWidth() / 2, UtilMath.getRounded(y + th / 2, th));
    }

    /**
     * Get the object at the specified mouse location.
     * 
     * @param mx The mouse horizontal location.
     * @param my The mouse vertical location.
     * @return The object reference, <code>null</code> if none.
     */
    public ObjectGame getObject(int mx, int my)
    {
        final MapTile<?> map = model.getMap();
        final CameraGame camera = model.getCamera();
        final int x = UtilMath.getRounded(mx, map.getTileWidth());
        final int y = UtilMath.getRounded(camera.getViewHeight() - my - 1, map.getTileHeight());
        for (final ObjectGame object : handlerObject.list())
        {
            if (hitObject(object, x, y, x + map.getTileWidth(), y + map.getTileHeight()))
            {
                return object;
            }
        }

        return null;
    }

    /**
     * Get the list of selected objects.
     * 
     * @return The selected objects.
     */
    public Collection<ObjectGame> getSelectedEnties()
    {
        final Collection<ObjectGame> list = new ArrayList<>(0);
        for (final ObjectGame object : handlerObject.list())
        {
            if (isSelected(object))
            {
                list.add(object);
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
     * Check the mouse over object flag.
     * 
     * @param object The object to check.
     * @return <code>true</code> if over, <code>false</code> else.
     */
    public boolean isOver(ObjectGame object)
    {
        if (objectsOver.containsKey(object))
        {
            return objectsOver.get(object).booleanValue();
        }
        return false;
    }

    /**
     * Check the mouse object selection flag.
     * 
     * @param object The object to check.
     * @return <code>true</code> if selected, <code>false</code> else.
     */
    public boolean isSelected(ObjectGame object)
    {
        if (objectsSelection.containsKey(object))
        {
            return objectsSelection.get(object).booleanValue();
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
     * Check if cursor is over at least one object.
     * 
     * @return <code>true</code> if over, <code>false</code> else.
     */
    public boolean hasOver()
    {
        return !objectsOver.isEmpty();
    }

    /**
     * Check if there it at least one object selected.
     * 
     * @return <code>true</code> if there is selected objects, <code>false</code> else.
     */
    public boolean hasSelection()
    {
        for (final ObjectGame object : handlerObject.list())
        {
            if (isSelected(object))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if object is hit.
     * 
     * @param object The object to check.
     * @param x1 First point x.
     * @param y1 First point y.
     * @param x2 Second point x.
     * @param y2 Second point y.
     * @return <code>true</code> if hit, <code>false</code> else.
     */
    private boolean hitObject(ObjectGame object, int x1, int y1, int x2, int y2)
    {
        final MapTile<?> map = model.getMap();
        final CameraGame camera = model.getCamera();
        if (object != null)
        {
            final int x = UtilMath.getRounded(object.getLocationIntX() - object.getWidth() / 2, map.getTileWidth())
                    - camera.getLocationIntX();
            final int y = UtilMath.getRounded(object.getLocationIntY(), map.getTileHeight()) - camera.getLocationIntY();
            final Rectangle r1 = Geom.createRectangle(x1, y1, x2 - x1, y2 - y1);
            final Rectangle r2 = Geom.createRectangle(x, y, object.getWidth(), object.getHeight());
            if (r1.intersects(r2))
            {
                return true;
            }
        }
        return false;
    }
}
