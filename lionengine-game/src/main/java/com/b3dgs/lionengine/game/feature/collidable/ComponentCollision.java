/*
 * Copyright (C) 2013-2018 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.collidable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.b3dgs.lionengine.game.feature.ComponentUpdater;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.Handlables;
import com.b3dgs.lionengine.game.feature.HandlerListener;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.TransformableListener;
import com.b3dgs.lionengine.geom.Point;

/**
 * Default collision component implementation. Designed to check collision between {@link Collidable}.
 * Collision events are notified to {@link CollidableListener}.
 * 
 * @see Collidable
 * @see CollidableListener
 */
public class ComponentCollision implements ComponentUpdater, HandlerListener, TransformableListener
{
    /** Location reduce factor (the higher it is, the lower is the map division per location). */
    static final double REDUCE_FACTOR = 128.0;

    /** Mapping reduced. */
    private final Map<Integer, Map<Point, Set<Collidable>>> collidables = new HashMap<>();
    /** To be notified. */
    private final List<Collided> toNotify = new ArrayList<>();

    /**
     * Create component.
     */
    public ComponentCollision()
    {
        super();
    }

    /**
     * Remove point. Remove list of no more collidable.
     * 
     * @param point The point to remove.
     * @param collidable The associated collidable.
     */
    private void removePoint(Point point, Collidable collidable)
    {
        final Integer group = collidable.getGroup();
        if (collidables.containsKey(group))
        {
            final Map<Point, Set<Collidable>> elements = collidables.get(group);
            if (elements.containsKey(point))
            {
                removePoint(point, collidable, elements, group);
            }
        }
    }

    /**
     * Remove point. Remove list of no more collidable.
     * 
     * @param point The point to remove.
     * @param collidable The associated collidable.
     * @param elements The current group elements.
     * @param group The current group.
     */
    private void removePoint(Point point, Collidable collidable, Map<Point, Set<Collidable>> elements, Integer group)
    {
        final Set<Collidable> points = elements.get(point);
        points.remove(collidable);
        if (points.isEmpty())
        {
            elements.remove(point);
        }
        if (elements.isEmpty())
        {
            collidables.remove(group);
        }
    }

    /**
     * Add point. Create empty list of not existing.
     * 
     * @param point The point to remove.
     * @param collidable The associated collidable.
     */
    private void addPoint(Point point, Collidable collidable)
    {
        final Integer group = collidable.getGroup();
        if (!collidables.containsKey(group))
        {
            collidables.put(group, new HashMap<Point, Set<Collidable>>());
        }
        final Map<Point, Set<Collidable>> elements = collidables.get(group);
        if (!elements.containsKey(point))
        {
            elements.put(point, new HashSet<Collidable>());
        }
        elements.get(point).add(collidable);
    }

    /*
     * ComponentUpdater
     */

    @Override
    public void update(double extrp, Handlables objects)
    {
        for (final Map<Point, Set<Collidable>> groups : collidables.values())
        {
            for (final Entry<Point, Set<Collidable>> current : groups.entrySet())
            {
                checkGroup(current);
            }
        }
        for (final Collided collided : toNotify)
        {
            collided.collidableA.notifyCollided(collided.collidableB, collided.collision);
        }
        toNotify.clear();
    }

    /**
     * Check elements in group.
     * 
     * @param current The elements in group.
     */
    private void checkGroup(Entry<Point, Set<Collidable>> current)
    {
        final Set<Collidable> elements = current.getValue();
        for (final Collidable objectA : elements)
        {
            checkOthers(objectA, current);
        }
    }

    /**
     * Check others element.
     * 
     * @param objectA The collidable reference.
     * @param current The current group to check.
     */
    private void checkOthers(Collidable objectA, Entry<Point, Set<Collidable>> current)
    {
        for (final Integer acceptedGroup : objectA.getAccepted())
        {
            // Others to compare only in accepted group
            if (collidables.containsKey(acceptedGroup))
            {
                checkOthers(objectA, current, acceptedGroup);
            }
        }
    }

    /**
     * Check others element.
     * 
     * @param objectA The collidable reference.
     * @param current The current group to check.
     * @param acceptedGroup The accepted group.
     */
    private void checkOthers(Collidable objectA, Entry<Point, Set<Collidable>> current, Integer acceptedGroup)
    {
        final Map<Point, Set<Collidable>> acceptedElements = collidables.get(acceptedGroup);
        final Point point = current.getKey();
        if (acceptedElements.containsKey(point))
        {
            checkPoint(objectA, acceptedElements, point);
        }
    }

    /**
     * Check others element at specified point.
     * 
     * @param objectA The collidable reference.
     * @param acceptedElements The current elements to check.
     * @param point The point to check.
     */
    private void checkPoint(Collidable objectA, Map<Point, Set<Collidable>> acceptedElements, Point point)
    {
        final Set<Collidable> others = acceptedElements.get(point);
        for (final Collidable objectB : others)
        {
            if (objectA != objectB)
            {
                final List<Collision> collisions = objectA.collide(objectB);
                for (final Collision collision : collisions)
                {
                    toNotify.add(new Collided(objectA, objectB, collision));
                }
            }
        }
    }

    /*
     * HandlerListener
     */

    @Override
    public void notifyHandlableAdded(Featurable featurable)
    {
        if (featurable.hasFeature(Collidable.class))
        {
            final Transformable transformable = featurable.getFeature(Transformable.class);
            transformable.addListener(this);
        }
    }

    @Override
    public void notifyHandlableRemoved(Featurable featurable)
    {
        if (featurable.hasFeature(Collidable.class))
        {
            final Transformable transformable = featurable.getFeature(Transformable.class);
            final Collidable collidable = transformable.getFeature(Collidable.class);

            removePoints(transformable.getOldX(), transformable.getOldY(), collidable);
            removePoints(transformable.getX(), transformable.getY(), collidable);

            transformable.removeListener(this);
        }
    }

    /*
     * TransformableListener
     */

    @Override
    public void notifyTransformed(Transformable transformable)
    {
        final Collidable collidable = transformable.getFeature(Collidable.class);

        final double oldX = transformable.getOldX();
        final double oldY = transformable.getOldY();

        final int oldMinX = (int) Math.floor(oldX / REDUCE_FACTOR);
        final int oldMinY = (int) Math.floor(oldY / REDUCE_FACTOR);
        final int oldMaxX = (int) Math.floor((oldX + collidable.getMaxWidth()) / REDUCE_FACTOR);
        final int oldMaxY = (int) Math.floor((oldY + collidable.getMaxHeight()) / REDUCE_FACTOR);

        final double x = transformable.getX();
        final double y = transformable.getY();

        final int minX = (int) Math.floor(x / REDUCE_FACTOR);
        final int minY = (int) Math.floor(y / REDUCE_FACTOR);
        final int maxX = (int) Math.floor((x + collidable.getMaxWidth()) / REDUCE_FACTOR);
        final int maxY = (int) Math.floor((y + collidable.getMaxHeight()) / REDUCE_FACTOR);

        if (oldMinX != minX || oldMinY != minY || oldMaxX != maxX || oldMaxY != maxY)
        {
            removePoints(oldMinX, oldMinY, oldMaxX, oldMaxY, collidable);
        }
        addPoints(minX, minY, maxX, maxY, collidable);
    }

    /**
     * Remove point and adjacent points depending of the collidable max collision size.
     * 
     * @param x The horizontal location.
     * @param y The vertical location.
     * @param collidable The collidable reference.
     */
    private void removePoints(double x, double y, Collidable collidable)
    {
        final int minX = (int) Math.floor(x / REDUCE_FACTOR);
        final int minY = (int) Math.floor(y / REDUCE_FACTOR);
        final int maxX = (int) Math.floor((x + collidable.getMaxWidth()) / REDUCE_FACTOR);
        final int maxY = (int) Math.floor((y + collidable.getMaxHeight()) / REDUCE_FACTOR);

        removePoints(minX, minY, maxX, maxY, collidable);
    }

    /**
     * Remove point and adjacent points depending of the collidable max collision size.
     * 
     * @param minX The min horizontal location.
     * @param minY The min vertical location.
     * @param maxX The min horizontal location.
     * @param maxY The min vertical location.
     * @param collidable The collidable reference.
     */
    private void removePoints(int minX, int minY, int maxX, int maxY, Collidable collidable)
    {
        removePoint(new Point(minX, minY), collidable);

        if (minX != maxX && minY == maxY)
        {
            removePoint(new Point(maxX, minY), collidable);
        }
        else if (minX == maxX && minY != maxY)
        {
            removePoint(new Point(minX, maxY), collidable);
        }
        else if (minX != maxX)
        {
            removePoint(new Point(minX, maxY), collidable);
            removePoint(new Point(maxX, minY), collidable);
            removePoint(new Point(maxX, maxY), collidable);
        }
    }

    /**
     * Add point and adjacent points depending of the collidable max collision size.
     * 
     * @param minX The min horizontal location.
     * @param minY The min vertical location.
     * @param maxX The min horizontal location.
     * @param maxY The min vertical location.
     * @param collidable The collidable reference.
     */
    private void addPoints(int minX, int minY, int maxX, int maxY, Collidable collidable)
    {
        addPoint(new Point(minX, minY), collidable);

        if (minX != maxX && minY == maxY)
        {
            addPoint(new Point(maxX, minY), collidable);
        }
        else if (minX == maxX && minY != maxY)
        {
            addPoint(new Point(minX, maxY), collidable);
        }
        else if (minX != maxX)
        {
            addPoint(new Point(minX, maxY), collidable);
            addPoint(new Point(maxX, minY), collidable);
            addPoint(new Point(maxX, maxY), collidable);
        }
    }

    /**
     * Collided data for postponed notification.
     */
    private static final class Collided
    {
        private final Collidable collidableA;
        private final Collidable collidableB;
        private final Collision collision;

        /**
         * Create collided data.
         * 
         * @param collidableA The first collidable.
         * @param collidableB The second collidable.
         * @param collision The associated collision.
         */
        private Collided(Collidable collidableA, Collidable collidableB, Collision collision)
        {
            super();

            this.collidableA = collidableA;
            this.collidableB = collidableB;
            this.collision = collision;
        }
    }
}
