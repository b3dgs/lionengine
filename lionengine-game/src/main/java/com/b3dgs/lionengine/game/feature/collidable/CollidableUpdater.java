/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import java.util.Set;

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
final class CollidableUpdater implements IdentifiableListener, CollisionChecker
{
    /**
     * Check if other collides with collision and its rectangle area.
     * 
     * @param origin The origin used.
     * @param collidable The collidable owner.
     * @param transformable The transformable owner.
     * @param with The collision to check with.
     * @param other The other collidable to check.
     * @param rectangle The collision rectangle.
     * @return <code>true</code> if collided, <code>false</code> else.
     */
    private static boolean collide(Origin origin,
                                   Collidable collidable,
                                   Transformable transformable,
                                   Collision with,
                                   Collidable other,
                                   Rectangle rectangle)
    {
        final Mirror mirror = getMirror(collidable, with);
        final int offsetX = getOffsetX(with, mirror);
        final int offsetY = getOffsetY(with, mirror);

        final double sh = origin.getX(transformable.getOldX() + offsetX, rectangle.getWidthReal());
        final double sv = origin.getY(transformable.getOldY() + offsetY, rectangle.getHeightReal());
        final double dh = origin.getX(transformable.getX() + offsetX, rectangle.getWidthReal()) - sh;
        final double dv = origin.getY(transformable.getY() + offsetY, rectangle.getHeightReal()) - sv;

        final double nh = Math.abs(dh);
        final double nv = Math.abs(dv);

        final int max = (int) Math.ceil(Math.max(nh, nv));
        final double sx;
        final double sy;

        if (Double.compare(nh, 1.0) >= 0 || Double.compare(nv, 1.0) >= 0)
        {
            sx = dh / max;
            sy = dv / max;
        }
        else
        {
            sx = dh;
            sy = dv;
        }

        final double oldX = rectangle.getX();
        final double oldY = rectangle.getY();

        for (int count = 0; count < max + 1; count++)
        {
            if (checkCollide(collidable, with, rectangle, other))
            {
                return true;
            }
            rectangle.translate(sx, sy);
        }
        rectangle.set(oldX, oldY, rectangle.getWidth(), rectangle.getHeight());

        return false;
    }

    /**
     * Check if current area collides other collidable area.
     * 
     * @param collidable The collidable.
     * @param with The collision to check with.
     * @param area The current area.
     * @param other The other collidable.
     * @return <code>true</code> if collided, <code>false</code> else.
     */
    private static boolean checkCollide(Collidable collidable, Collision with, Area area, Collidable other)
    {
        final List<Rectangle> others = other.getCollisionBounds();
        final List<Collision> othersColl = other.getCollisions();
        final int size = others.size();
        boolean collided = false;
        for (int i = 0; i < size; i++)
        {
            final Area current = others.get(i);
            final Collision by = othersColl.get(i);

            if ((area.contains(current) || area.intersects(current)) && other.isEnabled(by))
            {
                collidable.notifyCollided(other, with, by);

                if (other.getAccepted().contains(collidable.getGroup()))
                {
                    other.notifyCollided(collidable, by, with);
                }
                collided = true;
            }
        }
        return collided;
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
        if (collision.hasMirror())
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
    /** Collisions disabled list. */
    private final Set<Collision> disabled = new HashSet<>();
    /** Collisions cache. */
    private final List<Collision> cacheColls = new ArrayList<>();
    /** Bounding box cache. */
    private final List<Rectangle> cacheRect = new ArrayList<>();
    /** Enabled flag. */
    private boolean enabled;
    /** Disabled existence. */
    private boolean nonDisabledFlag = true;

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
     * @param cacheRectRender Cache for rendering.
     */
    private void update(Collision collision,
                        double x,
                        double y,
                        int width,
                        int height,
                        Map<Collision, Rectangle> cacheRectRender)
    {
        if (boxs.containsKey(collision))
        {
            final Rectangle rectangle = boxs.get(collision);
            rectangle.set(x, y, width, height);
            cacheRectRender.get(collision).set(x, y, width, height);
        }
        else
        {
            final Rectangle rectangle = new Rectangle(x, y, width, height);
            cacheColls.add(collision);
            cacheRect.add(rectangle);
            cacheRectRender.put(collision, new Rectangle(x, y, width, height));
            boxs.put(collision, rectangle);
        }
    }

    /**
     * Check if the collidable entered in collision with another one.
     * 
     * @param origin The origin used.
     * @param collidable The collidable owner.
     * @param transformable The transformable owner.
     * @param other The collidable reference.
     * @param accepted The accepted groups.
     * @return <code>true</code> if collided, <code>false</code> else.
     */
    public boolean collide(Origin origin,
                           Collidable collidable,
                           Transformable transformable,
                           Collidable other,
                           Collection<Integer> accepted)
    {
        boolean collide = false;
        if (enabled && other.isEnabled() && accepted.contains(other.getGroup()))
        {
            final int size = cacheColls.size();
            for (int i = 0; i < size; i++)
            {
                final Collision with = cacheColls.get(i);
                // CHECKSTYLE IGNORE LINE: NestedIfDepth
                if ((nonDisabledFlag || !disabled.contains(with))
                    && collide(origin, collidable, transformable, with, other, cacheRect.get(i)))
                {
                    collide = true;
                }
            }
        }
        return collide;
    }

    /**
     * Set the collision enabled flag.
     * 
     * @param enabled <code>true</code> to enable collision checking, <code>false</code> else.
     * @param collision The collision to change.
     */
    public void setEnabled(boolean enabled, Collision collision)
    {
        if (enabled)
        {
            disabled.remove(collision);
        }
        else
        {
            disabled.add(collision);
        }
        nonDisabledFlag = disabled.isEmpty();
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
     * Check if collision is enabled.
     * 
     * @return <code>true</code> if enabled, <code>false</code> else.
     */
    public boolean isEnabled()
    {
        return enabled;
    }

    /**
     * Get the collisions bounds.
     * 
     * @return The collisions bounds.
     */
    public List<Rectangle> getCollisionBounds()
    {
        return cacheRect;
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
     * @param cacheRectRender Cache for rendering.
     */
    public void notifyTransformed(Origin origin,
                                  FeatureProvider provider,
                                  Shape transformable,
                                  List<Collision> collisions,
                                  Map<Collision, Rectangle> cacheRectRender)
    {
        final int n = collisions.size();
        for (int i = 0; i < n; i++)
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

            final double x = origin.getX(transformable.getX() + offsetX, width);
            final double y = origin.getY(transformable.getY() + offsetY, height) + height;
            update(collision, x, y, width, height, cacheRectRender);
        }
    }

    @Override
    public void notifyDestroyed(Integer id)
    {
        enabled = false;
        boxs.clear();
        cacheColls.clear();
        cacheRect.clear();
        disabled.clear();
        nonDisabledFlag = true;
    }

    @Override
    public boolean isEnabled(Collision collision)
    {
        return nonDisabledFlag || !disabled.contains(collision);
    }
}
