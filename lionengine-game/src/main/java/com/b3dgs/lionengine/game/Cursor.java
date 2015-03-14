/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import com.b3dgs.lionengine.Localizable;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.InputDevicePointer;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.Renderable;
import com.b3dgs.lionengine.core.Updatable;
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
 * {@link #setSyncMode(boolean)}).</li>
 * <li><code>sensibility</code>: If the cursor is not synchronized on the system pointer, it can be defined (
 * {@link #setSensibility(double, double)}).</li>
 * <li><code>grid</code>: Represents the map grid, affecting {@link #getInTileX()} and {@link #getInTileY()}.</li>
 * <li><code>location</code>: The internal cursor position ({@link #setLocation(int, int)}).</li>
 * <li><code>surfaceId</code>: This is the current cursor surface that can be displayed ({@link #setSurfaceId(int)}).</li>
 * </ul>
 * </p>
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see InputDevicePointer
 * @see Image
 */
public class Cursor
        implements Localizable, Tiled, Updatable, Renderable
{
    /** Pointer reference. */
    private final InputDevicePointer pointer;
    /** Surface reference. */
    private final Image[] surface;
    /** Viewer reference. */
    private Viewer viewer;
    /** Cursor screen location x. */
    private double x;
    /** Cursor screen location y. */
    private double y;
    /** Cursor viewer relative location x. */
    private double viewX;
    /** Cursor viewer relative location y. */
    private double viewY;
    /** Synchronization mode. */
    private boolean sync;
    /** Horizontal sensibility. */
    private double sensibilityHorizontal;
    /** Vertical sensibility. */
    private double sensibilityVertical;
    /** Grid width. */
    private int gridWidth;
    /** Grid height. */
    private int gridHeight;
    /** Minimum location x. */
    private int minX;
    /** Minimum location y. */
    private int minY;
    /** Maximum location x. */
    private int maxX;
    /** Maximum location y. */
    private int maxY;
    /** Surface id. */
    private int surfaceId;
    /** Rendering horizontal offset. */
    private int offsetX;
    /** Rendering vertical offset. */
    private int offsetY;
    /** Location offset x. */
    private int offX;
    /** Location offset y. */
    private int offY;

    /**
     * Create a cursor fixed inside the resolution and load its images.
     * 
     * @param pointer The pointer reference (must not be <code>null</code>).
     * @param media The cursor media.
     * @param others The cursor medias list (containing the different cursor surfaces path).
     * @throws LionEngineException If invalid arguments or an error occurred when reading the image.
     */
    public Cursor(InputDevicePointer pointer, Media media, Media... others) throws LionEngineException
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

        sync = true;
        gridWidth = 1;
        gridHeight = 1;
        surfaceId = 0;
        offsetX = 0;
        offsetY = 0;
    }

    /**
     * Load the cursor images.
     * 
     * @param alpha <code>true</code> to enable alpha, <code>false</code> else.
     */
    public void load(boolean alpha)
    {
        for (final Image current : surface)
        {
            current.load(alpha);
        }
    }

    /**
     * Set the viewer reference.
     * 
     * @param viewer The viewer reference.
     */
    public void setViewer(Viewer viewer)
    {
        this.viewer = viewer;
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
     * Set the grid size. Will affect {@link #getInTileX()} and {@link #getInTileY()}.
     * 
     * @param width The horizontal grid (> 0).
     * @param height The vertical grid (> 0).
     * @throws LionEngineException If grid is not strictly positive.
     */
    public void setGrid(int width, int height) throws LionEngineException
    {
        Check.superiorStrict(width, 0);
        Check.superiorStrict(height, 0);
        gridWidth = width;
        gridHeight = height;
    }

    /**
     * Get the click number.
     * 
     * @return The click number.
     */
    public int getClick()
    {
        return pointer.getClick();
    }

    /**
     * Check if click is pressed.
     * 
     * @param click The click to check.
     * @return The pressed state.
     */
    public boolean hasClicked(int click)
    {
        return pointer.hasClicked(click);
    }

    /**
     * Check if click is pressed once only (ignore 'still clicked').
     * 
     * @param click The click to check.
     * @return The pressed state.
     */
    public boolean hasClickedOnce(int click)
    {
        return pointer.hasClickedOnce(click);
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
     * Get the current horizontal location on screen.
     * 
     * @return The current horizontal location on screen.
     */
    public double getScreenX()
    {
        return x;
    }

    /**
     * Get the current vertical location on screen.
     * 
     * @return The current vertical location on screen.
     */
    public double getScreenY()
    {
        return y;
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

    /*
     * Updatable
     */

    @Override
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
        if (viewer != null)
        {
            offX = (int) viewer.getX();
            offY = (int) viewer.getY();
        }

        x = UtilMath.fixBetween(x, minX, maxX);
        y = UtilMath.fixBetween(y, minY, maxY);
        viewX = x + offX;
        viewY = (viewer != null ? viewer.getHeight() : 0) - y + offY;
        for (final Image current : surface)
        {
            current.setLocation(x + offsetX, y + offsetY);
        }
    }

    /*
     * Renderable
     */

    @Override
    public void render(Graphic g)
    {
        surface[surfaceId].render(g);
    }

    /*
     * Localizable
     */

    @Override
    public double getX()
    {
        return viewX;
    }

    @Override
    public double getY()
    {
        return viewY;
    }

    @Override
    public int getWidth()
    {
        return gridWidth;
    }

    @Override
    public int getHeight()
    {
        return gridHeight;
    }

    /*
     * Tiled
     */

    @Override
    public int getInTileX()
    {
        return (int) viewX / gridWidth;
    }

    @Override
    public int getInTileY()
    {
        return (int) viewY / gridHeight;
    }

    @Override
    public int getInTileWidth()
    {
        return 1;
    }

    @Override
    public int getInTileHeight()
    {
        return 1;
    }
}
