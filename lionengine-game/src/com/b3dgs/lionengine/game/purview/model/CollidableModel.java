/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.purview.model;

import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.Line;
import com.b3dgs.lionengine.core.Polygon;
import com.b3dgs.lionengine.core.Rectangle;
import com.b3dgs.lionengine.game.CameraGame;
import com.b3dgs.lionengine.game.CollisionData;
import com.b3dgs.lionengine.game.purview.Collidable;
import com.b3dgs.lionengine.game.purview.Localizable;
import com.b3dgs.lionengine.game.purview.Mirrorable;
import com.b3dgs.lionengine.utility.UtilityMath;

/**
 * Default collidable model implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class CollidableModel
        implements Collidable
{
    /** Entity owning this model. */
    private final Localizable entity;
    /** Entity collision representation. */
    private final Polygon coll;
    /** Ray cast representation. */
    private final Line ray;
    /** The collision used. */
    private CollisionData collision;
    /** Temp entity bounding box from polygon. */
    private Rectangle box;

    /**
     * Create a collidable model.
     * 
     * @param entity The entity owning this model.
     */
    public CollidableModel(Localizable entity)
    {
        this.entity = entity;
        coll = UtilityMath.createPolygon();
        ray = UtilityMath.createLine();
        box = coll.getRectangle();
    }

    /*
     * CollidableModel
     */

    @Override
    public void updateCollision()
    {
        if (collision != null)
        {
            final int xCur = entity.getLocationIntX() + entity.getLocationOffsetX() - entity.getWidth() / 2;
            final int yCur = entity.getLocationIntY() + entity.getLocationOffsetY();
            final int xOld = (int) entity.getLocationOldX() + entity.getLocationOffsetX() - entity.getWidth() / 2;
            final int yOld = (int) entity.getLocationOldY() + entity.getLocationOffsetY();

            boolean mirror = false;
            if (collision.hasMirror() && entity instanceof Mirrorable)
            {
                mirror = ((Mirrorable) entity).getMirror();
            }
            final int offsetX = mirror ? -collision.getOffsetX() : collision.getOffsetX();
            final int offsetY = collision.getOffsetY();
            final int width = collision.getWidth();
            final int height = collision.getHeight();

            coll.reset();
            coll.addPoint(xCur + offsetX, yCur + offsetY);
            coll.addPoint(xCur + offsetX, yCur + offsetY + height);
            coll.addPoint(xCur + offsetX + width, yCur + offsetY + height);
            coll.addPoint(xCur + offsetX + width, yCur + offsetY);

            coll.addPoint(xOld + offsetX, yOld + offsetY);
            coll.addPoint(xOld + offsetX, yOld + offsetY + height);
            coll.addPoint(xOld + offsetX + width, yOld + offsetY + height);
            coll.addPoint(xOld + offsetX + width, yOld + offsetY);

            box = coll.getRectangle();

            final int sx = xOld + offsetX + width / 2;
            final int sy = yOld + offsetY + height / 2;
            final int ex = xCur + offsetX + width / 2;
            final int ey = yCur + offsetY + height / 2;
            ray.set(sx, sy, ex, ey);
        }
    }

    @Override
    public void setCollision(CollisionData collision)
    {
        if (collision == null)
        {
            coll.reset();
            box = coll.getRectangle();
            ray.set(0, 0, 0, 0);
        }
        this.collision = collision;
    }

    @Override
    public boolean collide(Collidable entity)
    {
        return box.intersects(entity.getCollisionBounds()) || box.contains(entity.getCollisionBounds());
    }

    @Override
    public boolean collide(Rectangle area)
    {
        return coll.intersects(area) || coll.contains(area);
    }

    @Override
    public void renderCollision(Graphic g, CameraGame camera)
    {
        final int x = camera.getViewpointX((int) box.getX());
        final int y = camera.getViewpointY((int) (box.getY() + box.getHeight()));
        g.drawRect(x, y, (int) box.getWidth(), (int) box.getHeight(), false);

        final int x1 = camera.getViewpointX((int) ray.getX1());
        final int y1 = camera.getViewpointY((int) ray.getY1());
        final int x2 = camera.getViewpointX((int) ray.getX2());
        final int y2 = camera.getViewpointY((int) ray.getY2());
        g.drawLine(x1, y1, x2, y2);
    }

    @Override
    public CollisionData getCollisionData()
    {
        return collision;
    }

    @Override
    public Rectangle getCollisionBounds()
    {
        return box;
    }

    @Override
    public Line getCollisionRay()
    {
        return ray;
    }
}
