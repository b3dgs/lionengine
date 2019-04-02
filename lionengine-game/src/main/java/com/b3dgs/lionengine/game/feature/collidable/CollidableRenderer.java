/*
 * Copyright (C) 2013-2018 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import java.util.List;
import java.util.Map;

import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Shape;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.geom.Area;
import com.b3dgs.lionengine.geom.Rectangle;
import com.b3dgs.lionengine.graphic.Graphic;

/**
 * Box ray cast collidable model implementation.
 */
final class CollidableRenderer
{
    /** Show collision flag. */
    private boolean showCollision;

    /**
     * Create a collidable renderer.
     */
    CollidableRenderer()
    {
        super();
    }

    /**
     * Render collisions.
     * 
     * @param g The graphic output.
     * @param viewer The viewer reference.
     * @param origin The origin used.
     * @param transformable The transformable owner.
     * @param cacheColls The computed collisions.
     * @param cacheRect The computed rectangles.
     */
    public void render(Graphic g,
                       Viewer viewer,
                       Origin origin,
                       Shape transformable,
                       List<Collision> cacheColls,
                       Map<Collision, Rectangle> cacheRect)
    {
        if (showCollision)
        {
            final int size = cacheColls.size();
            for (int i = 0; i < size; i++)
            {
                final Collision collision = cacheColls.get(i);
                if (Collision.AUTOMATIC == collision)
                {
                    final int x = (int) Math.round(origin.getX(viewer.getViewpointX(transformable.getX()),
                                                               transformable.getWidth()));
                    final int y = (int) Math.floor(origin.getY(viewer.getViewpointY(transformable.getY()),
                                                               transformable.getHeight()));
                    g.drawRect(x, y, transformable.getWidth(), transformable.getHeight(), false);
                }
                else
                {
                    final Area area = cacheRect.get(collision);
                    final int x = (int) Math.round(viewer.getViewpointX(area.getX()));
                    final int y = (int) Math.floor(viewer.getViewpointY(area.getY())) - area.getHeight();
                    g.drawRect(x, y, area.getWidth(), area.getHeight(), false);
                }
            }
        }
    }

    /**
     * Set the collision visibility.
     * 
     * @param visible <code>true</code> if visible, <code>false</code> else.
     */
    public void setCollisionVisibility(boolean visible)
    {
        showCollision = visible;
    }
}
