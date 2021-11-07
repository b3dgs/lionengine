/*
 * Copyright (C) 2013-2021 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Filter;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.ImageBuffer;

/**
 * Sprite implementation.
 */
class SpriteImpl implements Sprite
{
    /** Error already loaded. */
    static final String ERROR_ALREADY_LOADED = "Surface has already been loaded: ";

    /** Sprite file name (can be <code>null</code> created with existing surface). */
    private final Media media;
    /** Sprite current surface (<code>null</code> if not loaded from existing media). */
    private ImageBuffer surface;
    /** Sprite current surface stretched (<code>null</code> if not loaded from existing media). */
    private ImageBuffer surfaceStretched;
    /** Sprite original surface (<code>null</code> if surface unmodified). */
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
    /** Frame offsets x. */
    private int frameOffsetX;
    /** Frame offsets y. */
    private int frameOffsetY;
    /** Current angle in degree. */
    private int angle;
    /** Current angle horizontal anchor. */
    private int angleX;
    /** Current angle vertical anchor. */
    private int angleY;
    /** Alpha. */
    private int alpha = 255;

    /**
     * Internal constructor.
     * 
     * @param media The sprite media (must not be <code>null</code>).
     * @throws LionEngineException If media is <code>null</code> or image cannot be read.
     */
    SpriteImpl(Media media)
    {
        super();

        Check.notNull(media);

        this.media = media;

        final ImageHeader info = ImageInfo.get(media);
        width = info.getWidth();
        height = info.getHeight();
    }

    /**
     * Internal constructor.
     * 
     * @param surface The surface to share (must not be <code>null</code>).
     * @throws LionEngineException If surface is <code>null</code>.
     */
    SpriteImpl(ImageBuffer surface)
    {
        super();

        Check.notNull(surface);

        this.surface = surface;
        surfaceStretched = surface;
        width = surface.getWidth();
        height = surface.getHeight();
        media = null;
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
     */
    protected final void render(Graphic g, int x, int y, int w, int h, int ox, int oy)
    {
        g.setAlpha(alpha);
        if (Mirror.HORIZONTAL == mirror)
        {
            g.drawImage(surface,
                        x,
                        y,
                        x + w,
                        y + h,
                        ox * w + w,
                        oy * h,
                        ox * w,
                        oy * h + h,
                        -angle,
                        angleX + w,
                        angleY);
        }
        else if (Mirror.VERTICAL == mirror)
        {
            g.drawImage(surface, x, y, x + w, y + h, ox * w, oy * h + h, ox * w + w, oy * h, angle, angleX, angleY);
        }
        else
        {
            g.drawImage(surface, x, y, x + w, y + h, ox * w, oy * h, ox * w + w, oy * h + h, angle, angleX, angleY);
        }
        g.setAlpha(255);
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
        surfaceStretched = Graphics.resize(surfaceOriginal, newWidth, newHeight);
        surface = surfaceStretched;
    }

    /**
     * Compute the rendering point.
     * 
     * @param width The width to use.
     * @param height The height to use.
     */
    protected void computeRenderingPoint(int width, int height)
    {
        final int ox;
        final int oy;
        if (angle != 0 && Mirror.HORIZONTAL == mirror)
        {
            ox = frameOffsetX;
            oy = frameOffsetY;
        }
        else if (angle != 0 && Mirror.VERTICAL == mirror)
        {
            ox = -frameOffsetX;
            oy = -frameOffsetY;
        }
        else
        {
            ox = -frameOffsetX;
            oy = frameOffsetY;
        }
        rx = (int) Math.round(origin.getX(x, width)) + ox;
        ry = (int) Math.round(origin.getY(y, height)) + oy;
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
            surfaceStretched = surfaceOriginal;
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
        if (surface != null)
        {
            surface.dispose();
        }
    }

    @Override
    public final void stretch(double widthPercent, double heightPercent)
    {
        Check.superiorStrict(widthPercent, 0);
        Check.superiorStrict(heightPercent, 0);

        if (Double.compare(widthPercent, 100) != 0 || Double.compare(heightPercent, 100) != 0)
        {
            lazySurfaceBackup();
            final int newWidth = (int) Math.floor(surfaceOriginal.getWidth() * widthPercent / 100.0);
            final int newHeight = (int) Math.floor(surfaceOriginal.getHeight() * heightPercent / 100.0);
            stretch(newWidth, newHeight);
        }
    }

    @Override
    public final void rotate(int angle)
    {
        this.angle = UtilMath.wrapAngle(angle);
    }

    @Override
    public final void filter(Filter filter)
    {
        Check.notNull(filter);

        lazySurfaceBackup();
        surface = filter.filter(surfaceStretched);
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
    public void setFrameOffsets(int offsetX, int offsetY)
    {
        frameOffsetX = offsetX;
        frameOffsetY = offsetY;
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
        surface = Graphics.applyMask(surfaceStretched, mask);
    }

    @Override
    public final void setAlpha(int alpha)
    {
        Check.superiorOrEqual(alpha, 0);
        Check.inferiorOrEqual(alpha, 255);

        this.alpha = alpha;
    }

    @Override
    public void setAngleAnchor(int angleX, int angleY)
    {
        this.angleX = angleX;
        this.angleY = angleY;
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
        final SpriteImpl other = (SpriteImpl) object;
        return surface == other.surface;
    }
}
