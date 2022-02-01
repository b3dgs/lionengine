/*
 * Copyright (C) 2013-2022 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.lionengine.game.feature.collidable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.b3dgs.lionengine.game.feature.ComponentUpdater;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.Handlables;
import com.b3dgs.lionengine.game.feature.HandlerListener;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.TransformableListener;
import com.b3dgs.lionengine.geom.Area;
import com.b3dgs.lionengine.geom.Point;
import com.b3dgs.lionengine.geom.Rectangle;

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
    static final double REDUCE_FACTOR = 256.0;
    /** Maximum horizontal cache. */
    private static final int MAX_CACHE_X = 127;
    /** Maximum vertical cache. */
    private static final int MAX_CACHE_Y = 63;

    /**
     * Check elements inside area.
     * 
     * @param elements The elements to check.
     * @param area The area to check.
     * @param inside The elements inside the area found.
     */
    private static void checkInside(List<Collidable> elements, Area area, Collection<Collidable> inside)
    {
        for (int i = 0; i < elements.size(); i++)
        {
            final Collidable current = elements.get(i);
            final List<Rectangle> bounds = current.getCollisionBounds();
            for (int j = 0; j < bounds.size(); j++)
            {
                final Rectangle bound = bounds.get(j);
                if (area.intersects(bound) || area.contains(bound))
                {
                    inside.add(current);
                }
            }
        }
    }

    /**
     * Convert real position value to index.
     * 
     * @param value The real position value.
     * @return The index value.
     */
    private static int getIndex(double value)
    {
        return (int) Math.floor(value / REDUCE_FACTOR);
    }

    /** Cached points. */
    private final Point[][] cache = new Point[MAX_CACHE_X + 1][MAX_CACHE_Y + 1];
    /** Mapping reduced. */
    private final Map<Integer, Map<Point, List<Collidable>>> collidables = new HashMap<>();
    /** Already collided mapping. */
    private final Map<Collidable, Collidable> done = new HashMap<>(1);
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
     * Get elements inside area.
     * 
     * @param area The area used.
     * @return The elements inside area.
     */
    public Collection<Collidable> getInside(Area area)
    {
        final Collection<Collidable> inside = new HashSet<>();
        final Collection<Point> points = getPoints(area);

        for (final Map<Point, List<Collidable>> groups : collidables.values())
        {
            for (final Point point : points)
            {
                final List<Collidable> elements = groups.get(point);
                if (elements != null)
                {
                    checkInside(elements, area, inside);
                }
            }
        }

        return inside;
    }

    /**
     * Get point representing the area.
     * 
     * @param area The area reference.
     * @return The area divided in point depending of reduce factor.
     */
    private Collection<Point> getPoints(Area area)
    {
        final Collection<Point> points = new HashSet<>(4);
        final int oldMinX = getIndex(area.getX() - area.getWidth());
        final int oldMinY = getIndex(area.getY() - area.getHeight());
        final int oldMaxX = getIndex(area.getX() + area.getWidth());
        final int oldMaxY = getIndex(area.getY() + area.getHeight());

        for (int x = oldMinX; x <= oldMaxX; x++)
        {
            for (int y = oldMinY; y <= oldMaxY; y++)
            {
                points.add(cache(x, y));
            }
        }
        return points;
    }

    /**
     * Get cached point if possible.
     * 
     * @param x The horizontal location.
     * @param y The vertical location.
     * @return The cached point.
     */
    private Point cache(int x, int y)
    {
        if (x < 0 || y < 0 || x > MAX_CACHE_X || y > MAX_CACHE_Y)
        {
            return new Point(x, y);
        }
        if (cache[x][y] == null)
        {
            cache[x][y] = new Point(x, y);
        }
        return cache[x][y];
    }

    /**
     * Remove point and adjacent points depending of the collidable max collision size.
     * 
     * @param transformable The transformable reference.
     * @param collidable The collidable reference.
     */
    private void removePoints(Transformable transformable, Collidable collidable)
    {
        final int oldMinX = getIndex(transformable.getOldX() - collidable.getMaxWidth());
        final int oldMinY = getIndex(transformable.getOldY() - collidable.getMaxHeight());
        final int oldMaxX = getIndex(transformable.getOldX() + collidable.getMaxWidth());
        final int oldMaxY = getIndex(transformable.getOldY() + collidable.getMaxHeight());

        for (int x = oldMinX; x <= oldMaxX; x++)
        {
            for (int y = oldMinY; y <= oldMaxY; y++)
            {
                removePoint(cache(x, y), collidable);
            }
        }
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
            final Map<Point, List<Collidable>> elements = collidables.get(group);
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
    private void removePoint(Point point, Collidable collidable, Map<Point, List<Collidable>> elements, Integer group)
    {
        final List<Collidable> points = elements.get(point);
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
     * Add point and adjacent points depending of the collidable max collision size.
     * 
     * @param transformable The transformable reference.
     * @param collidable The collidable reference.
     */
    private void addPoints(Transformable transformable, Collidable collidable)
    {
        final int minX = getIndex(transformable.getX() - collidable.getMaxWidth());
        final int minY = getIndex(transformable.getY() - collidable.getMaxHeight());
        final int maxX = getIndex(transformable.getX() + collidable.getMaxWidth());
        final int maxY = getIndex(transformable.getY() + collidable.getMaxHeight());

        for (int x = minX; x <= maxX; x++)
        {
            for (int y = minY; y <= maxY; y++)
            {
                addPoint(cache(x, y), collidable);
            }
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
        Map<Point, List<Collidable>> elements = collidables.get(group);
        if (elements == null)
        {
            elements = new HashMap<>();
            collidables.put(group, elements);
        }

        List<Collidable> list = elements.get(point);
        if (list == null)
        {
            list = new ArrayList<>();
            elements.put(point, list);
        }

        if (!list.contains(collidable))
        {
            list.add(collidable);
        }
    }

    /**
     * Check elements in group.
     * 
     * @param current The elements in group.
     */
    private void checkGroup(Entry<Point, List<Collidable>> current)
    {
        final List<Collidable> elements = current.getValue();
        final int count = elements.size();
        for (int i = 0; i < count; i++)
        {
            final Collidable collidable = elements.get(i);
            if (collidable.isEnabled())
            {
                checkOthers(elements.get(i), current);
            }
        }
    }

    /**
     * Check others element.
     * 
     * @param objectA The collidable reference.
     * @param current The current group to check.
     */
    private void checkOthers(Collidable objectA, Entry<Point, List<Collidable>> current)
    {
        final List<Integer> accepted = objectA.getAccepted();
        final int count = accepted.size();
        for (int i = 0; i < count; i++)
        {
            final Integer acceptedGroup = accepted.get(i);

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
    private void checkOthers(Collidable objectA, Entry<Point, List<Collidable>> current, Integer acceptedGroup)
    {
        final Map<Point, List<Collidable>> acceptedElements = collidables.get(acceptedGroup);
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
    private void checkPoint(Collidable objectA, Map<Point, List<Collidable>> acceptedElements, Point point)
    {
        final List<Collidable> others = acceptedElements.get(point);
        for (int o = 0; o < others.size(); o++)
        {
            final Collidable objectB = others.get(o);

            // Ensures not already collided with object with other point (because of subdivision mapping)
            if (objectB.isEnabled() && objectA != objectB && done.get(objectA) != objectB)
            {
                final List<CollisionCouple> collisions = objectA.collide(objectB);
                final int count = collisions.size();
                for (int i = 0; i < count; i++)
                {
                    toNotify.add(new Collided(objectA, objectB, collisions.get(i)));
                    done.put(objectA, objectB);
                }
            }
        }
    }

    /*
     * ComponentUpdater
     */

    @Override
    public void update(double extrp, Handlables objects)
    {
        done.clear();
        for (final Map<Point, List<Collidable>> groups : collidables.values())
        {
            for (final Entry<Point, List<Collidable>> current : groups.entrySet())
            {
                checkGroup(current);
            }
        }
        for (final Collided collided : toNotify)
        {
            collided.collidableA.notifyCollided(collided.collidableB, collided.with, collided.by);
        }
        toNotify.clear();
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

            removePoints(transformable, collidable);

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

        removePoints(transformable, collidable);
        addPoints(transformable, collidable);
    }

    /**
     * Collided data for postponed notification.
     */
    private static final class Collided
    {
        private final Collidable collidableA;
        private final Collidable collidableB;
        private final Collision with;
        private final Collision by;

        /**
         * Create collided data.
         * 
         * @param collidableA The first collidable.
         * @param collidableB The second collidable.
         * @param collision The associated collision.
         */
        private Collided(Collidable collidableA, Collidable collidableB, CollisionCouple collision)
        {
            super();

            this.collidableA = collidableA;
            this.collidableB = collidableB;
            with = collision.getWith();
            by = collision.getBy();
        }
    }
}
