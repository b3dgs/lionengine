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
package com.b3dgs.lionengine.game;

import java.util.HashMap;
import java.util.Map;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Resource;
import com.b3dgs.lionengine.core.drawable.Drawable;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Image;
import com.b3dgs.lionengine.graphic.Renderable;

/**
 * Cursor rendering implementation.
 */
final class CursorRenderer implements Resource, Renderable
{
    /** Surface ID not found error. */
    private static final String ERROR_SURFACE_ID = "Undefined surface id:";

    /** Surface reference. */
    private final Map<Integer, Image> surfaces = new HashMap<>();
    /** Surface id. */
    private Integer surfaceId;
    /** Current surface. */
    private Image surface;
    /** Next surface. */
    private Image nextSurface;
    /** Rendering horizontal offset. */
    private int offsetX;
    /** Rendering vertical offset. */
    private int offsetY;
    /** Visibility flag. */
    private boolean visible = true;

    /**
     * Create a cursor renderer.
     */
    CursorRenderer()
    {
        super();
    }

    /**
     * Update renderer location and surface ID.
     * 
     * @param screenX The horizontal location on screen.
     * @param screenY The vertical location on screen.
     */
    public void update(double screenX, double screenY)
    {
        for (final Image current : surfaces.values())
        {
            current.setLocation(screenX + offsetX, screenY + offsetY);
        }
        if (nextSurface != null)
        {
            surface = nextSurface;
            nextSurface = null;
        }
    }

    /**
     * Add a cursor image. Once there are no more images to add, a call to {@link #load()} will be necessary.
     * 
     * @param id The cursor id.
     * @param media The cursor media.
     * @throws LionEngineException If invalid media.
     */
    public void addImage(int id, Media media)
    {
        final Integer key = Integer.valueOf(id);
        surfaces.put(key, Drawable.loadImage(media));
        if (surfaceId == null)
        {
            surfaceId = key;
        }
    }

    /**
     * Set the surface id to render with {@link #render(Graphic)}.
     * 
     * @param surfaceId The surface id number (must be strictly positive).
     * @throws LionEngineException If invalid id value or not found.
     */
    public void setSurfaceId(int surfaceId)
    {
        Check.superiorOrEqual(surfaceId, 0);
        this.surfaceId = Integer.valueOf(surfaceId);
        if (surfaces.containsKey(this.surfaceId))
        {
            nextSurface = surfaces.get(this.surfaceId);
        }
        else
        {
            throw new LionEngineException(ERROR_SURFACE_ID + surfaceId);
        }
    }

    /**
     * Set the rendering offsets value (allows to apply an offset depending of the cursor surface).
     * 
     * @param ox The horizontal offset.
     * @param oy The vertical offset.
     */
    public void setRenderingOffset(int ox, int oy)
    {
        offsetX = ox;
        offsetY = oy;
    }

    /**
     * Set the visibility.
     * 
     * @param visible <code>true</code> to show, <code>false</code> to hide.
     */
    public void setVisible(boolean visible)
    {
        this.visible = visible;
    }

    /**
     * Get the current surface id used for rendering.
     * 
     * @return The current surface id.
     */
    public Integer getSurfaceId()
    {
        return surfaceId;
    }

    /*
     * Resource
     */

    @Override
    public void load()
    {
        for (final Image current : surfaces.values())
        {
            current.load();
            current.prepare();
            if (surface == null)
            {
                surface = current;
            }
        }
    }

    @Override
    public boolean isLoaded()
    {
        for (final Image current : surfaces.values())
        {
            if (current.isLoaded())
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public void dispose()
    {
        for (final Image current : surfaces.values())
        {
            current.dispose();
        }
        surfaces.clear();
    }

    /*
     * Renderable
     */

    @Override
    public void render(Graphic g)
    {
        if (visible)
        {
            surface.render(g);
        }
    }
}
