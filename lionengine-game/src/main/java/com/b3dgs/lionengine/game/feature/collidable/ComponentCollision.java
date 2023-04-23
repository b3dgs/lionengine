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

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.game.feature.ComponentRenderer;
import com.b3dgs.lionengine.game.feature.ComponentUpdater;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.Handlables;
import com.b3dgs.lionengine.game.feature.HandlerListener;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.TransformableListener;
import com.b3dgs.lionengine.geom.Area;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Renderable;
import com.b3dgs.lionengine.graphic.RenderableVoid;

/**
 * Default collision component implementation. Designed to check collision between {@link Collidable}.
 * Collision events are notified to {@link CollidableListener}.
 * 
 * @see Collidable
 * @see CollidableListener
 */
public class ComponentCollision implements ComponentUpdater, ComponentRenderer, HandlerListener, TransformableListener
{
    private static final int TREE_SIZE = 16384;

    /** Mapping reduced by group as list index. */
    private final QuadTree collidables;
    /** Rendering. */
    private Renderable renderable = RenderableVoid.getInstance();

    /**
     * Create component.
     * 
     * @param viewer The viewer reference (must not be <code>null</code>).
     */
    public ComponentCollision(Viewer viewer)
    {
        super();

        Check.notNull(viewer);

        collidables = new QuadTree(viewer, TREE_SIZE);
    }

    /**
     * Set rendering visible.
     * 
     * @param visible <code>true</code> if visible, <code>false</code> else.
     */
    public void setVisible(boolean visible)
    {
        if (visible)
        {
            renderable = collidables::render;
        }
        else
        {
            renderable = RenderableVoid.getInstance();
        }
    }

    /**
     * Get elements inside area.
     * 
     * @param area The area used.
     * @return The elements inside area.
     */
    public List<Collidable> getInside(Area area)
    {
        return collidables.getInside(area);
    }

    /*
     * ComponentUpdater
     */

    @Override
    public void update(double extrp, Handlables objects)
    {
        collidables.compute();
    }

    /*
     * ComponentRenderer
     */

    @Override
    public void render(Graphic g, Handlables featurables)
    {
        g.setColor(ColorRgba.BLUE);
        renderable.render(g);
    }

    /*
     * HandlerListener
     */

    @Override
    public void notifyHandlableAdded(Featurable featurable)
    {
        if (featurable.hasFeature(Collidable.class))
        {
            final Transformable transformable = featurable.getFeature(Transformable.class);
            transformable.addListener(this);
        }
    }

    @Override
    public void notifyHandlableRemoved(Featurable featurable)
    {
        if (featurable.hasFeature(Collidable.class))
        {
            final Transformable transformable = featurable.getFeature(Transformable.class);
            transformable.removeListener(this);

            final Collidable collidable = featurable.getFeature(Collidable.class);
            collidables.remove(transformable, collidable);
        }
    }

    /*
     * TransformableListener
     */

    @Override
    public void notifyTransformed(Transformable transformable)
    {
        final Collidable collidable = transformable.getFeature(Collidable.class);
        if (collidable.isEnabled())
        {
            collidables.move(transformable, collidable);
        }
    }
}
