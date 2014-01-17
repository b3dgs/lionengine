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
package com.b3dgs.lionengine.game.purview.model;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Line;
import com.b3dgs.lionengine.Polygon;
import com.b3dgs.lionengine.Rectangle;
import com.b3dgs.lionengine.core.UtilityMath;
import com.b3dgs.lionengine.game.CameraGame;
import com.b3dgs.lionengine.game.CollisionData;
import com.b3dgs.lionengine.game.purview.Collidable;
import com.b3dgs.lionengine.game.purview.Localizable;
import com.b3dgs.lionengine.game.purview.Mirrorable;

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
            final int xCur = entity.getLocationIntX() - entity.getWidth() / 2;
            final int yCur = entity.getLocationIntY();
            final int xOld = (int) entity.getLocationOldX() - entity.getWidth() / 2;
            final int yOld = (int) entity.getLocationOldY();

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

        for (final Line line : coll.getPoints())
        {
            final int x3 = camera.getViewpointX((int) line.getX1());
            final int y3 = camera.getViewpointY((int) line.getY1());
            final int x4 = camera.getViewpointX((int) line.getX2());
            final int y4 = camera.getViewpointY((int) line.getY2());
            g.drawLine(x3, y3, x4, y4);
        }
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
