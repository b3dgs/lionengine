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
package com.b3dgs.lionengine.game.trait;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.game.Collision;
import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.component.ComponentCollisionListener;
import com.b3dgs.lionengine.game.handler.ObjectGame;
import com.b3dgs.lionengine.geom.Geom;
import com.b3dgs.lionengine.geom.Polygon;
import com.b3dgs.lionengine.geom.Rectangle;

/**
 * Box ray cast collidable model implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class CollidableModel
        extends TraitModel
        implements Collidable
{
    /** Transformable owning this model. */
    private final Transformable transformable;
    /** The viewer reference. */
    private final Viewer viewer;
    /** The collision listener reference. */
    private final Collection<ComponentCollisionListener> listeners;
    /** The collisions used. */
    private final Collection<Collision> collisions;
    /** The ignored collidables. */
    private final Collection<Collidable> ignored;
    /** Collision representation. */
    private final Map<Collision, Polygon> polygons;
    /** Temp bounding box from polygon. */
    private final Map<Collision, Rectangle> boxs;
    /** Origin used. */
    private Origin origin;
    /** Show collision flag. */
    private boolean showCollision;

    /**
     * Create a collidable model.
     * <p>
     * The owner must have the following {@link Trait}:
     * </p>
     * <ul>
     * <li>{@link Transformable}</li>
     * </ul>
     * The {@link Services} must provide the following services:
     * </p>
     * <ul>
     * <li>{@link Viewer}</li>
     * </ul>
     * 
     * @param owner The owner reference.
     * @param services The services reference.
     * @throws LionEngineException If missing {@link Trait}.
     */
    public CollidableModel(ObjectGame owner, Services services) throws LionEngineException
    {
        super(owner);
        listeners = new ArrayList<>();
        collisions = new ArrayList<>();
        ignored = new HashSet<>();
        polygons = new HashMap<>();
        boxs = new HashMap<>();
        transformable = getTrait(Transformable.class);
        viewer = services.get(Viewer.class);
        origin = Origin.TOP_LEFT;
        showCollision = false;
    }

    /*
     * Collidable
     */

    @Override
    public void addListener(ComponentCollisionListener listener)
    {
        listeners.add(listener);
    }

    @Override
    public void addCollision(Collision collision)
    {
        collisions.add(collision);
        polygons.put(collision, Geom.createPolygon());
    }

    @Override
    public void addIgnore(Collidable collidable)
    {
        ignored.add(collidable);
    }

    @Override
    public void update(double extrp)
    {
        for (final Collision collision : collisions)
        {
            final double xCur = transformable.getX();
            final double yCur = transformable.getY();
            final double xOld = transformable.getOldX();
            final double yOld = transformable.getOldY();

            Mirror mirror = Mirror.NONE;
            if (collision.hasMirror() && transformable instanceof Mirrorable)
            {
                mirror = ((Mirrorable) transformable).getMirror();
            }
            final int offsetX = mirror == Mirror.HORIZONTAL ? -collision.getOffsetX() : collision.getOffsetX();
            final int offsetY = collision.getOffsetY();
            final int width = collision.getWidth();
            final int height = collision.getHeight();

            final Polygon coll = polygons.get(collision);
            coll.reset();

            coll.addPoint(origin.getX(xCur + offsetX, width), origin.getY(yCur + offsetY, height));
            coll.addPoint(origin.getX(xCur + offsetX, width), origin.getY(yCur + offsetY + height, height));
            coll.addPoint(origin.getX(xCur + offsetX + width, width), origin.getY(yCur + offsetY + height, height));
            coll.addPoint(origin.getX(xCur + offsetX + width, width), origin.getY(yCur + offsetY, height));

            coll.addPoint(origin.getX(xOld + offsetX, width), origin.getY(yOld + offsetY, height));
            coll.addPoint(origin.getX(xOld + offsetX, width), origin.getY(yOld + offsetY + height, height));
            coll.addPoint(origin.getX(xOld + offsetX + width, width), origin.getY(yOld + offsetY + height, height));
            coll.addPoint(origin.getX(xOld + offsetX + width, width), origin.getY(yOld + offsetY, height));

            boxs.put(collision, coll.getRectangle());
        }
    }

    @Override
    public Collision collide(Collidable collidable)
    {
        if (!ignored.contains(collidable))
        {
            for (final Map.Entry<Collision, Rectangle> current : boxs.entrySet())
            {
                final Rectangle box = current.getValue();
                for (final Rectangle other : collidable.getCollisionBounds())
                {
                    if (box.intersects(other) || box.contains(other))
                    {
                        return current.getKey();
                    }
                }
            }
        }
        return null;
    }

    @Override
    public void render(Graphic g)
    {
        if (showCollision)
        {
            for (final Rectangle box : boxs.values())
            {
                final double x = viewer.getViewpointX(box.getX());
                final double y = viewer.getViewpointY(box.getY() + box.getHeight());
                g.drawRect((int) x, (int) y, (int) box.getWidth(), (int) box.getHeight(), false);
            }
        }
    }

    @Override
    public void setOrigin(Origin origin)
    {
        this.origin = origin;
    }

    @Override
    public void setCollisionVisibility(boolean visible)
    {
        showCollision = visible;
    }

    @Override
    public Iterable<Collision> getCollisions()
    {
        return collisions;
    }

    @Override
    public Iterable<Rectangle> getCollisionBounds()
    {
        return boxs.values();
    }

    @Override
    public void notifyCollided(Collidable collidable)
    {
        for (final ComponentCollisionListener listener : listeners)
        {
            listener.notifyCollided(collidable);
        }
    }
}
