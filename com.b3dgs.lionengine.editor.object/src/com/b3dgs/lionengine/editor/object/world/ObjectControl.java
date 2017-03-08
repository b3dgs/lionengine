/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.editor.object.world;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.editor.ObjectRepresentation;
import com.b3dgs.lionengine.editor.object.ObjectsTester;
import com.b3dgs.lionengine.editor.project.ProjectModel;
import com.b3dgs.lionengine.editor.utility.UtilWorld;
import com.b3dgs.lionengine.game.Camera;
import com.b3dgs.lionengine.game.Featurable;
import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.feature.Factory;
import com.b3dgs.lionengine.game.feature.Handler;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.Refreshable;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.geom.Point;
import com.b3dgs.lionengine.geom.Rectangle;
import com.b3dgs.lionengine.util.UtilMath;

/**
 * Allows to control the object on the editor with the mouse.
 */
public class ObjectControl
{
    /** Mouse over object flag. */
    private final Map<Transformable, Boolean> objectsOver = new HashMap<>();
    /** Mouse selection object flag. */
    private final Map<Transformable, Boolean> objectsSelection = new HashMap<>();
    /** Camera reference. */
    private final Camera camera;
    /** Factory reference. */
    private final Factory factory;
    /** Map reference. */
    private final MapTile map;
    /** Handler object. */
    private final Handler handler;
    /** Moving object flag. */
    private boolean dragging;

    /**
     * Create the object control from object handler.
     * 
     * @param services The services reference.
     */
    public ObjectControl(Services services)
    {
        camera = services.get(Camera.class);
        factory = services.get(Factory.class);
        map = services.get(MapTile.class);
        handler = services.get(Handler.class);
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
        final ObjectRepresentation object = getFeaturable(mx, my);
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
        if (!dragging)
        {
            dragging = true;
        }
        for (final Transformable transformable : handler.get(Transformable.class))
        {
            if (isSelected(transformable))
            {
                transformable.moveLocation(1.0, mx - (double) oldMx, (double) oldMy - my);
                transformable.getFeature(Refreshable.class).update(1.0);
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
    public void addAt(int mx, int my)
    {
        final Media media = ProjectModel.INSTANCE.getSelection();
        if (ObjectsTester.isObjectFile(media))
        {
            try
            {
                final ObjectRepresentation object = factory.create(media, ObjectRepresentation.class);
                final Point point = UtilWorld.getPoint(camera, mx, my);
                final Transformable transformable = object.getFeature(Transformable.class);
                transformable.teleport(UtilMath.getRounded(point.getX(), map.getTileWidth()),
                                       UtilMath.getRounded(point.getY(), map.getTileHeight()));
                transformable.getFeature(Refreshable.class).update(1.0);
                handler.add(object);
            }
            catch (final LionEngineException exception)
            {
                Verbose.exception(exception, media.getPath());
            }
        }
    }

    /**
     * Remove the featurable at the specified location if exists.
     * 
     * @param mx The mouse horizontal location.
     * @param my The mouse vertical location.
     */
    public void removeFrom(int mx, int my)
    {
        final Featurable featurable = getFeaturable(mx, my);
        if (featurable != null)
        {
            featurable.getFeature(Identifiable.class).destroy();
        }
    }

    /**
     * Select all objects inside the selection area.
     * 
     * @param selectionArea The selection area.
     */
    public void selectObjects(Rectangle selectionArea)
    {
        for (final ObjectRepresentation object : handler.get(ObjectRepresentation.class))
        {
            final Rectangle rectangle = object.getRectangle();
            if (selectionArea.contains(rectangle) || selectionArea.intersects(rectangle))
            {
                setObjectSelection(object, true);
            }
        }
    }

    /**
     * Unselect objects.
     */
    public void unselectObjects()
    {
        for (final ObjectRepresentation object : handler.get(ObjectRepresentation.class))
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
    public void setMouseOver(ObjectRepresentation object, boolean over)
    {
        objectsOver.put(object.getFeature(Transformable.class), Boolean.valueOf(over));
    }

    /**
     * Set the object selected state.
     * 
     * @param object The object reference.
     * @param selected <code>true</code> if selected, <code>false</code> else.
     */
    public void setObjectSelection(ObjectRepresentation object, boolean selected)
    {
        objectsSelection.put(object.getFeature(Transformable.class), Boolean.valueOf(selected));
    }

    /**
     * Get the object at the specified mouse location.
     * 
     * @param mx The mouse horizontal location.
     * @param my The mouse vertical location.
     * @return The object reference, <code>null</code> if none.
     */
    public ObjectRepresentation getFeaturable(int mx, int my)
    {
        for (final ObjectRepresentation object : handler.get(ObjectRepresentation.class))
        {
            if (object.getRectangle().contains(mx, my))
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
    public Collection<Transformable> getSelectedObjects()
    {
        final Collection<Transformable> list = new ArrayList<>(0);
        for (final Transformable transformable : handler.get(Transformable.class))
        {
            if (isSelected(transformable))
            {
                list.add(transformable);
            }
        }
        return list;
    }

    /**
     * Check the mouse over object flag.
     * 
     * @param transformable The featurable to check.
     * @return <code>true</code> if over, <code>false</code> else.
     */
    public boolean isOver(Transformable transformable)
    {
        if (objectsOver.containsKey(transformable))
        {
            return objectsOver.get(transformable).booleanValue();
        }
        return false;
    }

    /**
     * Check the mouse object selection flag.
     * 
     * @param transformable The featurable to check.
     * @return <code>true</code> if selected, <code>false</code> else.
     */
    public boolean isSelected(Transformable transformable)
    {
        if (objectsSelection.containsKey(transformable))
        {
            return objectsSelection.get(transformable).booleanValue();
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
        for (final Featurable featurable : handler.values())
        {
            if (isSelected(featurable.getFeature(Transformable.class)))
            {
                return true;
            }
        }
        return false;
    }
}
