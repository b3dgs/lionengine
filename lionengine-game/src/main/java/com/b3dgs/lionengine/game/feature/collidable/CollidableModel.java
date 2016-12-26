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
package com.b3dgs.lionengine.game.feature.collidable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.game.Featurable;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.Setup;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.IdentifiableListener;
import com.b3dgs.lionengine.game.feature.Mirrorable;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.TransformableListener;
import com.b3dgs.lionengine.geom.Rectangle;
import com.b3dgs.lionengine.graphic.Graphic;

/**
 * Box ray cast collidable model implementation.
 */
public class CollidableModel extends FeatureModel
                             implements Collidable, Recyclable, TransformableListener, IdentifiableListener
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

    /**
     * Get the horizontal offset.
     * 
     * @param collision The collision reference.
     * @param mirror The mirror used.
     * @return The offset value depending of mirror.
     */
    private static int getOffsetX(Collision collision, Mirror mirror)
    {
        if (mirror == Mirror.HORIZONTAL)
        {
            return -collision.getOffsetX();
        }
        return collision.getOffsetX();
    }

    /**
     * Get the vertical offset.
     * 
     * @param collision The collision reference.
     * @param mirror The mirror used.
     * @return The offset value depending of mirror.
     */
    private static int getOffsetY(Collision collision, Mirror mirror)
    {
        if (mirror == Mirror.VERTICAL)
        {
            return -collision.getOffsetY();
        }
        return collision.getOffsetY();
    }

    /** The collision listener reference. */
    private final List<CollidableListener> listeners = new ArrayList<CollidableListener>();
    /** The collisions used. */
    private final List<Collision> collisions = new ArrayList<Collision>();
    /** The accepted groups. */
    private final Collection<Integer> accepted = new HashSet<Integer>();
    /** The accepted groups as list. */
    private final List<Integer> acceptedList = new ArrayList<Integer>();
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
    /** Max width. */
    private int maxWidth;
    /** Max height. */
    private int maxHeight;
    /** Enabled flag. */
    private boolean enabled;
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
        collisions.addAll(CollisionConfig.imports(setup).getCollisions());

        recycle();
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
        if (norm < 0 || norm > 0)
        {
            sx = dh / norm;
            sy = dv / norm;
        }
        else
        {
            sx = 0;
            sy = 0;
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

    /**
     * Get the collision mirror.
     * 
     * @param collision The collision reference.
     * @return The collision mirror, {@link Mirror#NONE} if undefined.
     */
    private Mirror getMirror(Collision collision)
    {
        if (collision.hasMirror() && hasFeature(Mirrorable.class))
        {
            return getFeature(Mirrorable.class).getMirror();
        }
        return Mirror.NONE;
    }

    /**
     * Update the collision box.
     * 
     * @param collision The collision reference.
     * @param x The horizontal location.
     * @param y The vertical location.
     * @param width The collision width.
     * @param height The collision height.
     */
    private void update(Collision collision, double x, double y, int width, int height)
    {
        if (boxs.containsKey(collision))
        {
            final Rectangle rectangle = boxs.get(collision);
            rectangle.set(x, y, width, height);
        }
        else
        {
            final Rectangle rectangle = new Rectangle(x, y, width, height);
            cacheColls.add(collision);
            cacheRect.add(rectangle);
            boxs.put(collision, rectangle);
        }
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
        transformable.addListener(this);

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
    public void addAccept(int group)
    {
        final Integer id = Integer.valueOf(group);
        accepted.add(id);
        acceptedList.add(id);
    }

    @Override
    public void removeAccept(int group)
    {
        final Integer id = Integer.valueOf(group);
        accepted.remove(id);
        acceptedList.remove(id);
    }

    @Override
    public Collision collide(Collidable other)
    {
        if (enabled && accepted.contains(other.getGroup()))
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
        final int length = listeners.size();
        for (int i = 0; i < length; i++)
        {
            final CollidableListener listener = listeners.get(i);
            listener.notifyCollided(collidable);
        }
    }

    @Override
    public Integer getGroup()
    {
        return group;
    }

    @Override
    public List<Integer> getAccepted()
    {
        return acceptedList;
    }

    @Override
    public int getMaxWidth()
    {
        return maxWidth;
    }

    @Override
    public int getMaxHeight()
    {
        return maxHeight;
    }

    /*
     * Recyclable
     */

    @Override
    public final void recycle()
    {
        enabled = true;
    }

    /*
     * TransformableListener
     */

    @Override
    public void notifyTransformed(Transformable transformable)
    {
        if (enabled)
        {
            final int length = collisions.size();
            for (int i = 0; i < length; i++)
            {
                final Collision collision = collisions.get(i);

                final Mirror mirror = getMirror(collision);
                final int offsetX = getOffsetX(collision, mirror);
                final int offsetY = getOffsetY(collision, mirror);
                final int width = collision.getWidth();
                final int height = collision.getHeight();
                if (width > maxWidth)
                {
                    maxWidth = width;
                }
                if (height > maxHeight)
                {
                    maxHeight = height;
                }
                final double x = origin.getX(transformable.getOldX() + offsetX, width);
                final double y = origin.getY(transformable.getOldY() + offsetY, height);

                update(collision, x, y, width, height);
            }
        }
    }

    /*
     * IdentifiableListener
     */

    @Override
    public void notifyDestroyed(Integer id)
    {
        enabled = false;
        boxs.clear();
        cacheColls.clear();
        cacheRect.clear();
    }
}
