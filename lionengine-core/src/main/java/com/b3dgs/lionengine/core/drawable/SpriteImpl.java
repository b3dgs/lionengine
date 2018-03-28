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
package com.b3dgs.lionengine.core.drawable;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Localizable;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Filter;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.ImageBuffer;
import com.b3dgs.lionengine.graphic.ImageHeader;
import com.b3dgs.lionengine.graphic.ImageInfo;
import com.b3dgs.lionengine.graphic.Sprite;
import com.b3dgs.lionengine.graphic.UtilColor;

/**
 * Sprite implementation.
 */
class SpriteImpl implements Sprite
{
    /** Error already loaded. */
    private static final String ERROR_ALREADY_LOADED = "Surface has already been loaded: ";

    /** Sprite file name. */
    private final Media media;
    /** Sprite current surface (<code>null</code> if not loaded). */
    private volatile ImageBuffer surface;
    /** Sprite original surface. */
    private ImageBuffer surfaceOriginal;
    /** Origin point. */
    private Origin origin = Origin.TOP_LEFT;
    /** Mirror flag. */
    private Mirror mirror = Mirror.NONE;
    /** Sprite horizontal position. */
    private double x;
    /** Sprite vertical position. */
    private double y;
    /** Sprite width. */
    private int width;
    /** Sprite height. */
    private int height;
    /** Render horizontal position. */
    private int rx;
    /** Render vertical position. */
    private int ry;
    /** Sprite raw data (used for alpha, can be <code>null</code>). */
    private int[][] rgb;
    /** First alpha. */
    private boolean firstAlpha;

    /**
     * Internal constructor.
     * 
     * @param media The sprite media (must not be <code>null</code>).
     * @throws LionEngineException If media is <code>null</code> or image cannot be read.
     */
    SpriteImpl(Media media)
    {
        super();

        final ImageHeader info = ImageInfo.get(media);
        width = info.getWidth();
        height = info.getHeight();
        rgb = null;
        this.media = media;
    }

    /**
     * Internal constructor.
     * 
     * @param surface The surface to share (must not be <code>null</code>).
     * @throws LionEngineException If surface is <code>null</code>.
     */
    SpriteImpl(ImageBuffer surface)
    {
        Check.notNull(surface);

        this.surface = surface;
        width = surface.getWidth();
        height = surface.getHeight();
        media = null;
        rgb = null;
    }

    /**
     * Render an extract of a surface to a specified destination.
     * 
     * @param g The graphic output.
     * @param x The horizontal destination.
     * @param y The vertical destination.
     * @param w The width extract.
     * @param h The height extract.
     * @param ox The horizontal offset (width count).
     * @param oy The vertical offset (height count).
     * @throws LionEngineException If mirror error.
     */
    protected final void render(Graphic g, int x, int y, int w, int h, int ox, int oy)
    {
        if (Mirror.HORIZONTAL == mirror)
        {
            g.drawImage(surface, x, y, x + w, y + h, ox * w + w, oy * h, ox * w, oy * h + h);
        }
        else if (Mirror.VERTICAL == mirror)
        {
            g.drawImage(surface, x, y, x + w, y + h, ox * w, oy * h + h, ox * w + w, oy * h);
        }
        else
        {
            g.drawImage(surface, x, y, x + w, y + h, ox * w, oy * h, ox * w + w, oy * h + h);
        }
    }

    /**
     * Stretch the surface with the specified new size.
     * 
     * @param newWidth The new width.
     * @param newHeight The new height.
     */
    protected void stretch(int newWidth, int newHeight)
    {
        width = newWidth;
        height = newHeight;
        surface = Graphics.resize(surfaceOriginal, newWidth, newHeight);
    }

    /**
     * Compute the rendering point.
     * 
     * @param width The width to use.
     * @param height The height to use.
     */
    protected void computeRenderingPoint(int width, int height)
    {
        rx = (int) Math.floor(origin.getX(x, width));
        ry = (int) Math.floor(origin.getY(y, height));
    }

    /**
     * Get the horizontal rendering point.
     * 
     * @return The horizontal rendering point.
     */
    protected int getRenderX()
    {
        return rx;
    }

    /**
     * Get the vertical rendering point.
     * 
     * @return The vertical rendering point.
     */
    protected int getRenderY()
    {
        return ry;
    }

    /**
     * Backup the original surface before modification only if needed.
     */
    private void lazySurfaceBackup()
    {
        if (surfaceOriginal == null)
        {
            surfaceOriginal = Graphics.getImageBuffer(surface);
        }
    }

    /*
     * Sprite
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
        surface.dispose();
    }

    @Override
    public final void stretch(double widthPercent, double heightPercent)
    {
        Check.superiorStrict(widthPercent, 0);
        Check.superiorStrict(heightPercent, 0);

        if (Double.compare(widthPercent, 100) != 0 || Double.compare(heightPercent, 100) != 0)
        {
            final int newWidth = (int) Math.floor(width * widthPercent / 100.0);
            final int newHeight = (int) Math.floor(height * heightPercent / 100.0);
            lazySurfaceBackup();
            stretch(newWidth, newHeight);
        }
    }

    @Override
    public final void rotate(int angle)
    {
        lazySurfaceBackup();
        surface = Graphics.rotate(surfaceOriginal, angle);
        width = surface.getWidth();
        height = surface.getHeight();
    }

    @Override
    public final void filter(Filter filter)
    {
        Check.notNull(filter);

        lazySurfaceBackup();
        surface = filter.filter(surfaceOriginal);
        width = surface.getWidth();
        height = surface.getHeight();
    }

    @Override
    public void render(Graphic g)
    {
        render(g, rx, ry, width, height, 0, 0);
    }

    @Override
    public void setOrigin(Origin origin)
    {
        Check.notNull(origin);

        this.origin = origin;
        computeRenderingPoint(width, height);
    }

    @Override
    public final void setLocation(double x, double y)
    {
        this.x = x;
        this.y = y;
        computeRenderingPoint(width, height);
    }

    @Override
    public final void setLocation(Viewer viewer, Localizable localizable)
    {
        setLocation(viewer.getViewpointX(localizable.getX()), viewer.getViewpointY(localizable.getY()));
    }

    @Override
    public final void setTransparency(ColorRgba mask)
    {
        lazySurfaceBackup();
        surface = Graphics.applyMask(surfaceOriginal, mask);
    }

    @Override
    public final void setAlpha(int alpha)
    {
        Check.superiorOrEqual(alpha, 0);
        Check.inferiorOrEqual(alpha, 255);

        setFade(alpha, -255);
    }

    @Override
    public final void setFade(int alpha, int fade)
    {
        if (rgb == null)
        {
            rgb = new int[width][height];
            firstAlpha = true;
        }
        for (int cx = 0; cx < width; cx++)
        {
            for (int cy = 0; cy < height; cy++)
            {
                if (firstAlpha)
                {
                    lazySurfaceBackup();
                    rgb[cx][cy] = surfaceOriginal.getRgb(cx, cy);
                }
                final int alphaKey = 0x00ffffff;
                final int mc = Math.abs(alpha) << Constant.BYTE_4 | alphaKey;
                final int color = fade + alpha;
                surface.setRgb(cx, cy, UtilColor.inc(rgb[cx][cy], color, color, color) & mc);
            }
        }
        firstAlpha = false;
    }

    @Override
    public final void setMirror(Mirror mirror)
    {
        Check.notNull(mirror);

        this.mirror = mirror;
    }

    @Override
    public final Mirror getMirror()
    {
        return mirror;
    }

    @Override
    public final double getX()
    {
        return x;
    }

    @Override
    public final double getY()
    {
        return y;
    }

    @Override
    public final int getWidth()
    {
        return width;
    }

    @Override
    public final int getHeight()
    {
        return height;
    }

    @Override
    public final ImageBuffer getSurface()
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
        if (media != null)
        {
            result = prime * result + media.hashCode();
        }
        result = prime * result + width;
        result = prime * result + height;
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
        final SpriteImpl other = (SpriteImpl) object;
        return surface == other.surface && width == other.width && height == other.height;
    }
}
