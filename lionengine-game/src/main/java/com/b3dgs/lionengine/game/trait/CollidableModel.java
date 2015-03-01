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
import com.b3dgs.lionengine.game.configurer.ConfigCollisions;
import com.b3dgs.lionengine.game.configurer.Configurer;
import com.b3dgs.lionengine.game.object.ObjectGame;
import com.b3dgs.lionengine.game.object.Services;
import com.b3dgs.lionengine.geom.Geom;
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
    private final Collection<CollidableListener> listeners;
    /** The collisions used. */
    private final Collection<Collision> collisions;
    /** The ignored collidables. */
    private final Collection<Collidable> ignored;
    /** Temp bounding box from polygon. */
    private final Map<Collision, Rectangle> boxs;
    /** Origin used. */
    private Origin origin;
    /** Enabled flag. */
    private boolean enabled;
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
     * <p>
     * The {@link Configurer} must provide a valid configuration compatible with {@link ConfigCollisions}.
     * </p>
     * <p>
     * The {@link Services} must provide the following services:
     * </p>
     * <ul>
     * <li>{@link Viewer}</li>
     * </ul>
     * 
     * @param owner The owner reference.
     * @param configurer The configurer reference.
     * @param services The services reference.
     * @throws LionEngineException If missing {@link Trait} or {@link Services}.
     */
    public CollidableModel(ObjectGame owner, Configurer configurer, Services services) throws LionEngineException
    {
        super(owner);
        listeners = new ArrayList<>();
        collisions = new ArrayList<>();
        ignored = new HashSet<>();
        boxs = new HashMap<>();
        transformable = owner.getTrait(Transformable.class);
        viewer = services.get(Viewer.class);
        origin = Origin.TOP_LEFT;
        enabled = true;
        showCollision = false;
        for (final Collision collision : ConfigCollisions.create(configurer).getCollisions())
        {
            addCollision(collision);
        }
    }

    /*
     * Collidable
     */

    @Override
    public void addListener(CollidableListener listener)
    {
        listeners.add(listener);
    }

    @Override
    public void addCollision(Collision collision)
    {
        collisions.add(collision);
    }

    @Override
    public void addIgnore(Collidable collidable)
    {
        ignored.add(collidable);
    }

    @Override
    public void update(double extrp)
    {
        if (enabled)
        {
            for (final Collision collision : collisions)
            {
                Mirror mirror = Mirror.NONE;
                if (collision.hasMirror() && transformable instanceof Mirrorable)
                {
                    mirror = ((Mirrorable) transformable).getMirror();
                }
                final int offsetX = mirror == Mirror.HORIZONTAL ? -collision.getOffsetX() : collision.getOffsetX();
                final int offsetY = mirror == Mirror.VERTICAL ? -collision.getOffsetY() : collision.getOffsetY();
                final int width = collision.getWidth();
                final int height = collision.getHeight();

                final double x = origin.getX(transformable.getOldX() + offsetX, width);
                final double y = origin.getY(transformable.getOldY() + offsetY, height);
                if (boxs.containsKey(collision))
                {
                    final Rectangle rectangle = boxs.get(collision);
                    rectangle.set(x, y, width, height);
                }
                else
                {
                    final Rectangle rectangle = Geom.createRectangle(x, y, width, height);
                    boxs.put(collision, rectangle);
                }
            }
        }
    }

    @Override
    public Collision collide(Collidable collidable)
    {
        if (enabled && !ignored.contains(collidable))
        {
            for (final Map.Entry<Collision, Rectangle> current : boxs.entrySet())
            {
                final Collision collision = current.getKey();
                final Rectangle rectangle = current.getValue();

                final double sh = rectangle.getX();
                final double sv = rectangle.getY();
                final double dh = origin.getX(transformable.getX() + collision.getOffsetX(), rectangle.getWidth()) - sh;
                final double dv = origin.getY(transformable.getY() + collision.getOffsetY(), rectangle.getHeight())
                        - sv;
                final double norm = Math.sqrt(dh * dh + dv * dv);
                final double sx = dh / norm;
                final double sy = dv / norm;

                for (int count = 0; count < norm; count++)
                {
                    for (final Rectangle other : collidable.getCollisionBounds())
                    {
                        if (rectangle.intersects(other) || rectangle.contains(other))
                        {
                            return current.getKey();
                        }
                    }
                    rectangle.translate(sx, sy);
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
            for (final Map.Entry<Collision, Rectangle> current : boxs.entrySet())
            {
                final Collision collision = current.getKey();

                final int x = (int) origin.getX(viewer.getViewpointX(transformable.getX() + collision.getOffsetX()),
                        collision.getWidth());
                final int y = (int) origin.getY(viewer.getViewpointY(transformable.getY() + collision.getOffsetY()),
                        collision.getHeight());
                g.drawRect(x, y, collision.getWidth(), collision.getHeight(), false);
            }
        }
    }

    @Override
    public void setOrigin(Origin origin)
    {
        this.origin = origin;
    }

    @Override
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
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
        for (final CollidableListener listener : listeners)
        {
            listener.notifyCollided(collidable);
        }
    }
}
