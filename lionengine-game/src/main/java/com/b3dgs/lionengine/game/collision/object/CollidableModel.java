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
import java.util.List;
import java.util.Map;

import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.FeatureProvider;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.mirrorable.Mirrorable;
import com.b3dgs.lionengine.game.feature.transformable.Transformable;
import com.b3dgs.lionengine.geom.Geom;
import com.b3dgs.lionengine.geom.Rectangle;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Viewer;

/**
 * Box ray cast collidable model implementation.
 */
public class CollidableModel extends FeatureModel implements Collidable
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
        final List<Rectangle> others = other.getCollisionBounds();
        final int size = others.size();
        for (int i = 0; i < size; i++)
        {
            final Rectangle current = others.get(i);
            if (rectangle.intersects(current))
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
    /** The ignored groups. */
    private final Collection<Integer> ignored = new HashSet<Integer>();
    /** Temp bounding box from polygon. */
    private final Map<Collision, Rectangle> boxs = new HashMap<Collision, Rectangle>();
    /** Collisions cache. */
    private final List<Collision> cacheColls = new ArrayList<Collision>();
    /** Bounding box cache. */
    private final List<Rectangle> cacheRect = new ArrayList<Rectangle>();
    /** Associated group ID. */
    private Integer group;
    /** Transformable owning this model. */
    private Transformable transformable;
    /** The viewer reference. */
    private Viewer viewer;
    /** Origin used. */
    private Origin origin = Origin.TOP_LEFT;
    /** Enabled flag. */
    private boolean enabled = true;
    /** Show collision flag. */
    private boolean showCollision;

    /**
     * Create a collidable model.
     * <p>
     * The {@link Services} must provide:
     * </p>
     * <ul>
     * <li>{@link Viewer}</li>
     * </ul>
     * <p>
     * The {@link Featurable} must have:
     * </p>
     * <ul>
     * <li>{@link Transformable}</li>
     * </ul>
     * <p>
     * If the {@link Featurable} is a {@link CollidableListener}, it will automatically
     * {@link #addListener(CollidableListener)} on it.
     * </p>
     * 
     * @param setup The setup reference, must provide a valid {@link CollisionConfig}.
     */
    public CollidableModel(Setup setup)
    {
        super();

        group = CollidableConfig.imports(setup);
        for (final Collision collision : CollisionConfig.imports(setup).getCollisions())
        {
            collisions.add(collision);
        }
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
        final double dh = origin.getX(transformable.getX() + collision.getOffsetX(), rectangle.getWidthReal()) - sh;
        final double dv = origin.getY(transformable.getY() + collision.getOffsetY(), rectangle.getHeightReal()) - sv;
        final double norm = Math.sqrt(dh * dh + dv * dv);
        final double sx;
        final double sy;
        if (norm == 0)
        {
            sx = 0;
            sy = 0;
        }
        else
        {
            sx = dh / norm;
            sy = dv / norm;
        }

        for (int count = 0; count <= norm; count++)
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
    public void prepare(FeatureProvider provider, Services services)
    {
        super.prepare(provider, services);

        viewer = services.get(Viewer.class);
        transformable = provider.getFeature(Transformable.class);

        if (provider instanceof CollidableListener)
        {
            addListener((CollidableListener) provider);
        }
    }

    @Override
    public void checkListener(Object listener)
    {
        super.checkListener(listener);

        if (listener instanceof CollidableListener)
        {
            addListener((CollidableListener) listener);
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
    public void addIgnore(int group)
    {
        ignored.add(Integer.valueOf(group));
    }

    @Override
    public void update(double extrp)
    {
        if (enabled)
        {
            for (final Collision collision : collisions)
            {
                Mirror mirror = Mirror.NONE;
                if (collision.hasMirror() && hasFeature(Mirrorable.class))
                {
                    mirror = getFeature(Mirrorable.class).getMirror();
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
                    cacheColls.add(collision);
                    cacheRect.add(rectangle);
                    boxs.put(collision, rectangle);
                }
            }
        }
    }

    @Override
    public Collision collide(Collidable other)
    {
        if (enabled && !ignored.contains(other.getGroup()))
        {
            final int size = cacheColls.size();
            for (int i = 0; i < size; i++)
            {
                final Collision collision = collide(other, cacheColls.get(i), cacheRect.get(i));
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
            final int size = cacheColls.size();
            for (int i = 0; i < size; i++)
            {
                final Collision collision = cacheColls.get(i);
                final int x = (int) origin.getX(viewer.getViewpointX(transformable.getX() + collision.getOffsetX()),
                                                collision.getWidth());
                final int y = (int) origin.getY(viewer.getViewpointY(transformable.getY() + collision.getOffsetY()),
                                                collision.getHeight());
                g.drawRect(x, y, collision.getWidth(), collision.getHeight(), false);
            }
        }
    }

    @Override
    public void setGroup(int group)
    {
        this.group = Integer.valueOf(group);
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
    public Collection<Collision> getCollisions()
    {
        return collisions;
    }

    @Override
    public List<Rectangle> getCollisionBounds()
    {
        return cacheRect;
    }

    @Override
    public void notifyCollided(Collidable collidable)
    {
        for (final CollidableListener listener : listeners)
        {
            listener.notifyCollided(collidable);
        }
    }

    @Override
    public Integer getGroup()
    {
        return group;
    }
}
