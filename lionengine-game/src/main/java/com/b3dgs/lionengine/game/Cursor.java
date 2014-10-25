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
package com.b3dgs.lionengine.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.InputDevicePointer;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.Image;

/**
 * Used to represent a pointer cursor, desynchronized from the system pointer or not. This way, it is possible to
 * set a specific sensibility. As the cursor surface is stored in an {@link Image}, the cursor can be rendered
 * immediately after the constructor call. It contains the following functionalities:
 * <p>
 * <ul>
 * <li><code>surface</code>: A cursor can contain many surfaces, but only the selected one is displayed.</li>
 * <li><code>area</code>: Represents the area where the cursor can move on. Its location can not exit this area (
 * {@link #setArea(int, int, int, int)}).</li>
 * <li><code>sync</code>: <code>true</code> if cursor is synchronized on the system pointer, <code>false</code> not (
 * {@link Cursor#setSyncMode(boolean)}).</li>
 * <li><code>sensibility</code>: If the cursor is not synchronized on the system pointer, it can be defined (
 * {@link Cursor#setSensibility(double, double)}).</li>
 * <li><code>location</code>: The internal cursor position ({@link #setLocation(int, int)}).</li>
 * <li><code>surfaceId</code>: This is the current cursor surface that can be displayed (
 * {@link Cursor#setSurfaceId(int)}).</li>
 * </ul>
 * </p>
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see InputDevicePointer
 * @see Image
 */
public class Cursor
{
    /** Pointer reference. */
    private final InputDevicePointer pointer;
    /** Surface reference. */
    private final Image[] surface;
    /** Cursor location x. */
    private double x;
    /** Cursor location y. */
    private double y;
    /** Synchronization mode. */
    private boolean sync;
    /** Horizontal sensibility. */
    private double sensibilityHorizontal;
    /** Vertical sensibility. */
    private double sensibilityVertical;
    /** Minimum location x. */
    private int minX;
    /** Minimum location y. */
    private int minY;
    /** Maximum location x. */
    private int maxX;
    /** Maximum location y. */
    private int maxY;
    /** Click number. */
    private int click;
    /** Surface id. */
    private int surfaceId;
    /** Rendering horizontal offset. */
    private int offsetX;
    /** Rendering vertical offset. */
    private int offsetY;

    /**
     * Create a cursor fixed inside the resolution and load its images.
     * 
     * @param pointer The pointer reference (must not be <code>null</code>).
     * @param resolution The resolution used to know the screen limits.
     * @param media The cursor media.
     * @param others The cursor medias list (containing the different cursor surfaces path).
     * @throws LionEngineException If invalid arguments or an error occurred when reading the image.
     */
    public Cursor(InputDevicePointer pointer, Resolution resolution, Media media, Media... others)
            throws LionEngineException
    {
        this(pointer, 0, 0, resolution.getWidth(), resolution.getHeight(), media, others);
    }

    /**
     * Create a cursor and load its images.
     * 
     * @param pointer The pointer reference (must not be <code>null</code>).
     * @param minX The minimal x location on screen.
     * @param minY The minimal y location on screen.
     * @param maxX The maximal x location on screen.
     * @param maxY The maximal y location on screen.
     * @param media The cursor media.
     * @param others The cursor media list (containing the different cursor surfaces path).
     * @throws LionEngineException If invalid arguments or an error occurred when reading the image.
     */
    public Cursor(InputDevicePointer pointer, int minX, int minY, int maxX, int maxY, Media media, Media... others)
            throws LionEngineException
    {
        Check.notNull(pointer);
        Check.notNull(media);

        this.pointer = pointer;
        x = 0.0;
        y = 0.0;
        sensibilityHorizontal = 1.0;
        sensibilityVertical = 1.0;

        final Collection<Media> images = new ArrayList<>(1 + others.length);
        images.add(media);
        images.addAll(Arrays.asList(others));
        surface = new Image[images.size()];

        int i = 0;
        for (final Media current : images)
        {
            surface[i] = Drawable.loadImage(current);
            i++;
        }

        this.minX = Math.min(minX, maxX);
        this.minY = Math.min(minY, maxY);
        this.maxX = Math.max(maxX, minX);
        this.maxY = Math.max(maxY, minY);
        sync = true;
        surfaceId = 0;
        offsetX = 0;
        offsetY = 0;
    }

    /**
     * Update cursor position depending of pointer movement.
     * 
     * @param extrp The extrapolation value.
     */
    public void update(double extrp)
    {
        if (sync)
        {
            x = pointer.getX();
            y = pointer.getY();
        }
        else
        {
            x += pointer.getMoveX() * sensibilityHorizontal * extrp;
            y += pointer.getMoveY() * sensibilityVertical * extrp;
        }

        x = UtilMath.fixBetween(x, minX, maxX);
        y = UtilMath.fixBetween(y, minY, maxY);
        click = pointer.getClick();
    }

    /**
     * Render cursor on screen at its current location.
     * 
     * @param g The graphic output.
     */
    public void render(Graphic g)
    {
        surface[surfaceId].render(g, (int) x + offsetX, (int) y + offsetY);
    }

    /**
     * Set the cursor synchronization to the pointer.
     * 
     * @param sync The sync mode (<code>true</code> = sync to system pointer; <code>false</code> = internal movement).
     */
    public void setSyncMode(boolean sync)
    {
        this.sync = sync;
    }

    /**
     * Set cursor sensibility (move speed). Default value should be 1.0 (close to system sensibility).
     * 
     * @param sh The horizontal sensibility (>= 0.0).
     * @param sv The vertical sensibility (>= 0.0).
     */
    public void setSensibility(double sh, double sv)
    {
        sensibilityHorizontal = Math.max(0.0, sh);
        sensibilityVertical = Math.max(0.0, sv);
    }

    /**
     * Set cursor location.
     * 
     * @param x The horizontal location.
     * @param y The vertical location.
     */
    public void setLocation(int x, int y)
    {
        this.x = UtilMath.fixBetween(x, minX, maxX);
        this.y = UtilMath.fixBetween(y, minY, maxY);
    }

    /**
     * Set the surface id to render with {@link #render(Graphic)}.
     * 
     * @param surfaceId The surface id number (start at 0 which is default value).
     */
    public void setSurfaceId(int surfaceId)
    {
        this.surfaceId = Math.max(0, surfaceId);
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
     * Allows cursor to move only inside the specified area. The cursor location will not exceed this area.
     * 
     * @param minX The minimal x.
     * @param minY The minimal y.
     * @param maxX The maximal x.
     * @param maxY The maximal y.
     */
    public void setArea(int minX, int minY, int maxX, int maxY)
    {
        this.minX = Math.min(minX, maxX);
        this.minY = Math.min(minY, maxY);
        this.maxX = Math.max(maxX, minX);
        this.maxY = Math.max(maxY, minY);
    }

    /**
     * Return pointer click number.
     * 
     * @return The pointer click number.
     */
    public int getClick()
    {
        return click;
    }

    /**
     * Get the current surface id used for rendering.
     * 
     * @return The current surface id.
     */
    public int getSurfaceId()
    {
        return surfaceId;
    }

    /**
     * Get horizontal location.
     * 
     * @return The horizontal location.
     */
    public int getLocationX()
    {
        return (int) Math.floor(x);
    }

    /**
     * Get vertical location.
     * 
     * @return The vertical location.
     */
    public int getLocationY()
    {
        return (int) Math.floor(y);
    }

    /**
     * Get horizontal sensibility.
     * 
     * @return The horizontal sensibility.
     */
    public double getSensibilityHorizontal()
    {
        return sensibilityHorizontal;
    }

    /**
     * Get vertical sensibility.
     * 
     * @return The vertical sensibility.
     */
    public double getSensibilityVertical()
    {
        return sensibilityVertical;
    }

    /**
     * Check if the cursor is synchronized to the system pointer or not.
     * 
     * @return <code>true</code> = sync to the system pointer; <code>false</code> = internal movement.
     */
    public boolean isSynchronized()
    {
        return sync;
    }
}
