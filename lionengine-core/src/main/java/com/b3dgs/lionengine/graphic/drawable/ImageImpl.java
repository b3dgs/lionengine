/*
 * Copyright (C) 2013-2022 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.graphic.drawable;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Localizable;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.ImageBuffer;

/**
 * Image implementation.
 */
final class ImageImpl implements Image
{
    /** Error already loaded. */
    static final String ERROR_ALREADY_LOADED = "Image has already been loaded: ";

    /** Media file name (can be <code>null</code>). */
    private final Media media;
    /** Sprite width. */
    private final int width;
    /** Sprite height. */
    private final int height;
    /** Image surface (can be <code>null</code>). */
    private ImageBuffer surface;
    /** Origin point. */
    private Origin origin = Origin.TOP_LEFT;
    /** Image horizontal position. */
    private double x;
    /** Image vertical position. */
    private double y;
    /** Render horizontal position. */
    private int rx;
    /** Render vertical position. */
    private int ry;

    /**
     * Internal constructor.
     * 
     * @param media The image media (must not be <code>null</code>).
     * @throws LionEngineException If the media is <code>null</code>.
     */
    ImageImpl(Media media)
    {
        super();

        Check.notNull(media);

        final ImageHeader info = ImageInfo.get(media);
        width = info.getWidth();
        height = info.getHeight();
        this.media = media;
    }

    /**
     * Internal constructor.
     * 
     * @param surface The surface to share (must not be <code>null</code>).
     * @throws LionEngineException If the surface is <code>null</code>.
     */
    ImageImpl(ImageBuffer surface)
    {
        super();

        Check.notNull(surface);

        this.surface = surface;
        width = surface.getWidth();
        height = surface.getHeight();
        media = null;
    }

    /**
     * Compute the rendering point.
     * 
     * @param width The width to use.
     * @param height The height to use.
     */
    private void computeRenderingPoint(int width, int height)
    {
        rx = (int) Math.round(origin.getX(x, width));
        ry = (int) Math.round(origin.getY(y, height));
    }

    /**
     * Get the horizontal rendering point.
     * 
     * @return The horizontal rendering point.
     */
    int getRenderX()
    {
        return rx;
    }

    /**
     * Get the vertical rendering point.
     * 
     * @return The vertical rendering point.
     */
    int getRenderY()
    {
        return ry;
    }

    /*
     * Image
     */

    @Override
    public synchronized void load()
    {
        if (surface != null)
        {
            if (media != null)
            {
                throw new LionEngineException(media, ERROR_ALREADY_LOADED);
            }
            throw new LionEngineException(ERROR_ALREADY_LOADED);
        }
        surface = Graphics.getImageBuffer(media);
    }

    @Override
    public void prepare()
    {
        surface.prepare();
    }

    @Override
    public void dispose()
    {
        if (surface != null)
        {
            surface.dispose();
        }
    }

    @Override
    public void render(Graphic g)
    {
        g.drawImage(surface, rx, ry);
    }

    @Override
    public void setOrigin(Origin origin)
    {
        Check.notNull(origin);

        this.origin = origin;
        computeRenderingPoint(width, height);
    }

    @Override
    public void setLocation(double x, double y)
    {
        this.x = x;
        this.y = y;
        computeRenderingPoint(width, height);
    }

    @Override
    public void setLocation(Viewer viewer, Localizable localizable)
    {
        setLocation(viewer.getViewpointX(localizable.getX()), viewer.getViewpointY(localizable.getY()));
    }

    @Override
    public double getX()
    {
        return x;
    }

    @Override
    public double getY()
    {
        return y;
    }

    @Override
    public int getWidth()
    {
        return width;
    }

    @Override
    public int getHeight()
    {
        return height;
    }

    @Override
    public ImageBuffer getSurface()
    {
        return surface;
    }

    @Override
    public boolean isLoaded()
    {
        return surface != null;
    }

    /*
     * Object
     */

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        if (surface != null)
        {
            result = prime * result + surface.hashCode();
        }
        else
        {
            result = prime * result + media.hashCode();
        }
        return result;
    }

    @Override
    public boolean equals(Object object)
    {
        if (object == this)
        {
            return true;
        }
        if (object == null || object.getClass() != getClass())
        {
            return false;
        }
        final ImageImpl other = (ImageImpl) object;
        return surface == other.surface;
    }
}
