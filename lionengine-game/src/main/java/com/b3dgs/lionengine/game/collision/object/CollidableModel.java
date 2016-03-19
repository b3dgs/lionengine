/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.collision.object;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.game.object.ObjectGame;
import com.b3dgs.lionengine.game.object.Services;
import com.b3dgs.lionengine.game.object.TraitModel;
import com.b3dgs.lionengine.game.object.trait.mirrorable.Mirrorable;
import com.b3dgs.lionengine.game.object.trait.transformable.Transformable;
import com.b3dgs.lionengine.geom.Geom;
import com.b3dgs.lionengine.geom.Rectangle;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Viewer;

/**
 * Box ray cast collidable model implementation.
 * <p>
 * The {@link ObjectGame} owner must have the following {@link com.b3dgs.lionengine.game.object.Trait}:
 * </p>
 * <ul>
 * <li>{@link Transformable}</li>
 * </ul>
 * <p>
 * The {@link ObjectGame} owner must provide a valid {@link com.b3dgs.lionengine.game.Configurer} compatible
 * with {@link CollisionConfig}.
 * </p>
 * <p>
 * The {@link Services} must provide the following services:
 * </p>
 * <ul>
 * <li>{@link Viewer}</li>
 * </ul>
 * <p>
 * If the {@link ObjectGame} is a {@link CollidableListener}, it will automatically
 * {@link #addListener(CollidableListener)} on it.
 * </p>
 */
public class CollidableModel extends TraitModel implements Collidable
{
    /**
     * Check if current rectangle collides other collidable rectangles.
     * 
     * @param rectangle The current rectangle.
     * @param other The other collidable.
     * @return <code>true</code> if collide, <code>false</code> else.
     */
    private static boolean checkCollide(Rectangle rectangle, Collidable other)
    {
        for (final Rectangle current : other.getCollisionBounds())
        {
            if (rectangle.intersects(current) || rectangle.contains(current))
            {
                return true;
            }
        }
        return false;
    }

    /** The collision listener reference. */
    private final Collection<CollidableListener> listeners = new ArrayList<CollidableListener>();
    /** The collisions used. */
    private final Collection<Collision> collisions = new ArrayList<Collision>();
    /** The ignored collidables. */
    private final Collection<Collidable> ignored = new HashSet<Collidable>();
    /** Temp bounding box from polygon. */
    private final Map<Collision, Rectangle> boxs = new HashMap<Collision, Rectangle>();
    /** Transformable owning this model. */
    private Transformable transformable;
    /** The viewer reference. */
    private Viewer viewer;
    /** Origin used. */
    private Origin origin;
    /** Enabled flag. */
    private boolean enabled;
    /** Show collision flag. */
    private boolean showCollision;

    /**
     * Create a collidable model.
     */
    public CollidableModel()
    {
        super();
        origin = Origin.TOP_LEFT;
        enabled = true;
        showCollision = false;
    }

    /**
     * Check if other collides with collision and its rectangle area.
     * 
     * @param other The other collidable to check.
     * @param collision The collision to check with.
     * @param rectangle The collision rectangle.
     * @return The collision collides with other, <code>null</code> if none.
     */
    private Collision collide(Collidable other, Collision collision, Rectangle rectangle)
    {
        final double sh = rectangle.getX();
        final double sv = rectangle.getY();
        final double dh = origin.getX(transformable.getX() + collision.getOffsetX(), rectangle.getWidth()) - sh;
        final double dv = origin.getY(transformable.getY() + collision.getOffsetY(), rectangle.getHeight()) - sv;
        final double norm = Math.sqrt(dh * dh + dv * dv);
        final double sx = dh / norm;
        final double sy = dv / norm;

        for (int count = 0; count < norm; count++)
        {
            if (checkCollide(rectangle, other))
            {
                return collision;
            }
            rectangle.translate(sx, sy);
        }
        return null;
    }

    /*
     * Collidable
     */

    @Override
    public void prepare(ObjectGame owner, Services services)
    {
        super.prepare(owner, services);
        viewer = services.get(Viewer.class);
        for (final Collision collision : CollisionConfig.create(owner.getConfigurer()).getCollisions())
        {
            addCollision(collision);
        }
        transformable = owner.getTrait(Transformable.class);
        if (owner instanceof CollidableListener)
        {
            addListener((CollidableListener) owner);
        }
    }

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

                final int offsetX;
                if (mirror == Mirror.HORIZONTAL)
                {
                    offsetX = -collision.getOffsetX();
                }
                else
                {
                    offsetX = collision.getOffsetX();
                }

                final int offsetY;
                if (mirror == Mirror.VERTICAL)
                {
                    offsetY = -collision.getOffsetY();
                }
                else
                {
                    offsetY = collision.getOffsetY();
                }

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
    public Collision collide(Collidable other)
    {
        if (enabled && !ignored.contains(other))
        {
            for (final Map.Entry<Collision, Rectangle> current : boxs.entrySet())
            {
                final Collision collision = collide(other, current.getKey(), current.getValue());
                if (collision != null)
                {
                    return collision;
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
    public void notifyCollided(ObjectGame object)
    {
        for (final CollidableListener listener : listeners)
        {
            listener.notifyCollided(object);
        }
    }
}
