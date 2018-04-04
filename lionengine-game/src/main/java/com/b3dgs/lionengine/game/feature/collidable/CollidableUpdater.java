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
package com.b3dgs.lionengine.game.feature.collidable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Shape;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.feature.IdentifiableListener;
import com.b3dgs.lionengine.game.feature.Mirrorable;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.geom.Area;
import com.b3dgs.lionengine.geom.Rectangle;

/**
 * Box ray cast collidable model implementation.
 */
final class CollidableUpdater implements IdentifiableListener
{
    /**
     * Check if other collides with collision and its rectangle area.
     * 
     * @param origin The origin used.
     * @param provider The provider owner.
     * @param transformable The transformable owner.
     * @param other The other collidable to check.
     * @param collision The collision to check with.
     * @param rectangle The collision rectangle.
     * @return The collision collides with other, <code>null</code> if none.
     */
    private static Collision collide(Origin origin,
                                     FeatureProvider provider,
                                     Transformable transformable,
                                     Collidable other,
                                     Collision collision,
                                     Rectangle rectangle)
    {
        final Mirror mirror = getMirror(provider, collision);
        final int offsetX = getOffsetX(collision, mirror);
        final int offsetY = getOffsetY(collision, mirror);

        final double sh = rectangle.getX();
        final double sv = rectangle.getY();
        final double dh = origin.getX(transformable.getX() + offsetX, rectangle.getWidthReal()) - sh;
        final double dv = origin.getY(transformable.getY() + offsetY, rectangle.getHeightReal()) - sv;
        final double norm = Math.sqrt(dh * dh + dv * dv);
        final double sx;
        final double sy;
        if (Double.compare(norm, 0.0) == 0)
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

    /**
     * Check if current area collides other collidable area.
     * 
     * @param area The current area.
     * @param other The other collidable.
     * @return <code>true</code> if collide, <code>false</code> else.
     */
    private static boolean checkCollide(Area area, Collidable other)
    {
        final List<Area> others = other.getCollisionBounds();
        final int size = others.size();
        for (int i = 0; i < size; i++)
        {
            final Area current = others.get(i);
            if (area.intersects(current))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Get the collision mirror.
     * 
     * @param provider The provider owner.
     * @param collision The collision reference.
     * @return The collision mirror, {@link Mirror#NONE} if undefined.
     */
    private static Mirror getMirror(FeatureProvider provider, Collision collision)
    {
        if (collision.hasMirror() && provider.hasFeature(Mirrorable.class))
        {
            return provider.getFeature(Mirrorable.class).getMirror();
        }
        return Mirror.NONE;
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

    /** Temp bounding box from polygon. */
    private final Map<Collision, Rectangle> boxs = new HashMap<>();
    /** Collisions cache. */
    private final List<Collision> cacheColls = new ArrayList<>();
    /** Bounding box cache. */
    private final List<Rectangle> cacheRect = new ArrayList<>();
    /** Max width. */
    private int maxWidth;
    /** Max height. */
    private int maxHeight;
    /** Enabled flag. */
    private boolean enabled;

    /**
     * Create a collidable updater.
     */
    CollidableUpdater()
    {
        super();
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

    /**
     * Check if the collidable entered in collision with another one.
     * 
     * @param origin The origin used.
     * @param provider The provider owner.
     * @param transformable The transformable owner.
     * @param other The collidable reference.
     * @param accepted The accepted groups.
     * @return The collision found if collide, <code>null</code> if none.
     */
    public Collision collide(Origin origin,
                             FeatureProvider provider,
                             Transformable transformable,
                             Collidable other,
                             Collection<Integer> accepted)
    {
        if (enabled && accepted.contains(other.getGroup()))
        {
            final int size = cacheColls.size();
            for (int i = 0; i < size; i++)
            {
                final Collision collision = collide(origin,
                                                    provider,
                                                    transformable,
                                                    other,
                                                    cacheColls.get(i),
                                                    cacheRect.get(i));
                if (collision != null)
                {
                    return collision;
                }
            }
        }
        return null;
    }

    /**
     * Set the collision enabled flag.
     * 
     * @param enabled <code>true</code> to enable collision checking, <code>false</code> else.
     */
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    /**
     * Get the collisions bounds as read only.
     * 
     * @return The collisions bounds.
     */
    public List<Area> getCollisionBounds()
    {
        return Collections.unmodifiableList(cacheRect);
    }

    /**
     * Get the current max width.
     * 
     * @return The max width.
     */
    public int getMaxWidth()
    {
        return maxWidth;
    }

    /**
     * Get the current max height.
     * 
     * @return The max height.
     */
    public int getMaxHeight()
    {
        return maxHeight;
    }

    /**
     * Get current collision cache.
     * 
     * @return The collision cache.
     */
    public List<Collision> getCache()
    {
        return cacheColls;
    }

    /**
     * Notify transformable modification.
     * 
     * @param origin The origin used.
     * @param provider The provider owner.
     * @param transformable The modified transformable.
     * @param collisions The declared collisions.
     */
    public void notifyTransformed(Origin origin,
                                  FeatureProvider provider,
                                  Shape transformable,
                                  List<Collision> collisions)
    {
        if (enabled)
        {
            final int length = collisions.size();
            for (int i = 0; i < length; i++)
            {
                final Collision collision = collisions.get(i);

                final Mirror mirror = getMirror(provider, collision);
                final int offsetX = getOffsetX(collision, mirror);
                final int offsetY = getOffsetY(collision, mirror);
                final int width;
                final int height;
                if (Collision.AUTOMATIC == collision)
                {
                    width = transformable.getWidth();
                    height = transformable.getHeight();
                }
                else
                {
                    width = collision.getWidth();
                    height = collision.getHeight();
                }
                if (width > maxWidth)
                {
                    maxWidth = width;
                }
                if (height > maxHeight)
                {
                    maxHeight = height;
                }
                final double x = origin.getX(transformable.getX() + offsetX, collision.getWidth());
                final double y = origin.getY(transformable.getY() + offsetY, collision.getHeight());

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
