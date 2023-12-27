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

import java.util.List;
import java.util.Map;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Localizable;
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
    /**
     * Render collisions.
     * 
     * @param g The graphic output.
     * @param viewer The viewer reference.
     * @param origin The origin used.
     * @param transformable The transformable owner.
     * @param cacheColls The computed collisions.
     * @param cacheRect The computed rectangles.
     * @param checker The collision checker.
     */
    public static void render(Graphic g,
                              Viewer viewer,
                              Origin origin,
                              Shape transformable,
                              List<Collision> cacheColls,
                              Map<Collision, Rectangle> cacheRect,
                              CollisionChecker checker)
    {
        final int size = cacheColls.size();
        for (int i = 0; i < size; i++)
        {
            final Collision collision = cacheColls.get(i);
            if (Collision.AUTOMATIC == collision)
            {
                final int x = (int) Math.round(origin.getX(viewer.getViewpointX(transformable.getX()),
                                                           transformable.getWidth()));
                final int y = (int) Math.round(origin.getY(viewer.getViewpointY(transformable.getY()),
                                                           transformable.getHeight()));
                g.drawRect(x, y, transformable.getWidth(), transformable.getHeight(), false);
            }
            else if (checker.isEnabled(collision))
            {
                final Area area = cacheRect.get(collision);
                final int x = (int) Math.round(viewer.getViewpointX(area.getX()));
                final int y = (int) Math.round(viewer.getViewpointY(area.getY())) - area.getHeight();
                g.drawRect(x, y, area.getWidth(), area.getHeight(), false);
            }
        }
    }

    /**
     * Render collisions.
     * 
     * @param g The graphic output.
     * @param viewer The viewer reference.
     * @param origin The origin used.
     * @param transformable The transformable owner.
     * @param maxWidth The max width.
     * @param minHeight The min height.
     * @param maxHeight The max height.
     */
    public static void renderMax(Graphic g,
                                 Viewer viewer,
                                 Origin origin,
                                 Localizable transformable,
                                 int maxWidth,
                                 int minHeight,
                                 int maxHeight)
    {
        g.drawRect(viewer, origin, transformable.getX() - maxWidth / 2, transformable.getY() + minHeight, 1, 1, false);
        g.drawRect(viewer, origin, transformable.getX() + maxWidth / 2, transformable.getY() + minHeight, 1, 1, false);
        g.drawRect(viewer, origin, transformable.getX() - maxWidth / 2, transformable.getY() + maxHeight, 1, 1, false);
        g.drawRect(viewer, origin, transformable.getX() + maxWidth / 2, transformable.getY() + maxHeight, 1, 1, false);
    }

    /**
     * Create a collidable renderer.
     */
    private CollidableRenderer()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
