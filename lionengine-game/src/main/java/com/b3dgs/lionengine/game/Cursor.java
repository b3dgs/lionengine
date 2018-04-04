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

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Resource;
import com.b3dgs.lionengine.Shape;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Renderable;
import com.b3dgs.lionengine.graphic.drawable.Image;
import com.b3dgs.lionengine.io.InputDevicePointer;

/**
 * Used to represent a pointer cursor, desynchronized from the system pointer or not. This way, it is possible to
 * set a specific sensibility. As the cursor surface is stored in an {@link Image}, the cursor can be rendered
 * immediately after the constructor call. It contains the following functionalities:
 * <ul>
 * <li><code>surface</code>: A cursor can contain many surfaces, but only the selected one is displayed.</li>
 * <li><code>area</code>: Represents the area where the cursor can move on. Its location can not exit this area (
 * {@link #setArea(int, int, int, int)}).</li>
 * <li><code>sync</code>: <code>true</code> if cursor is synchronized on the system pointer, <code>false</code> not (
 * {@link #setSyncMode(boolean)}).</li>
 * <li><code>sensibility</code>: If the cursor is not synchronized on the system pointer, it can be defined (
 * {@link #setSensibility(double, double)}).</li>
 * <li><code>grid</code>: Represents the map grid.</li>
 * <li><code>location</code>: The internal cursor position ({@link #setLocation(int, int)}).</li>
 * <li>
 * <code>surfaceId</code>: This is the current cursor surface that can be displayed ({@link #setSurfaceId(int)}).</li>
 * </ul>
 * <p>
 * Usage example:
 * </p>
 * <ul>
 * <li>Create the cursor with {@link #Cursor()}.</li>
 * <li>Add images with {@link #addImage(int, Media)}.</li>
 * <li>Load added images {@link #load()}.</li>
 * <li>Set the input to use {@link #setInputDevice(InputDevicePointer)}.</li>
 * <li>Change the cursor image if when needed with {@link #setSurfaceId(int)}.</li>
 * <li>Define the screen area {@link #setArea(int, int, int, int)}.</li>
 * </ul>
 * 
 * @see InputDevicePointer
 * @see Image
 */
public class Cursor implements Resource, Shape, Updatable, Renderable
{
    /** Cursor renderer. */
    private final CursorRenderer renderer = new CursorRenderer();
    /** Pointer reference. */
    private InputDevicePointer pointer;
    /** Viewer reference. */
    private Viewer viewer;
    /** Cursor screen location x. */
    private double screenX;
    /** Cursor screen location y. */
    private double screenY;
    /** Cursor viewer relative location x. */
    private double x;
    /** Cursor viewer relative location y. */
    private double y;
    /** Synchronization mode. */
    private boolean sync = true;
    /** Horizontal sensibility. */
    private double sensibilityHorizontal = 1.0;
    /** Vertical sensibility. */
    private double sensibilityVertical = 1.0;
    /** Grid width. */
    private int gridWidth = 1;
    /** Grid height. */
    private int gridHeight = 1;
    /** Minimum location x. */
    private int minX = Integer.MIN_VALUE;
    /** Minimum location y. */
    private int minY = Integer.MIN_VALUE;
    /** Maximum location x. */
    private int maxX = Integer.MAX_VALUE;
    /** Maximum location y. */
    private int maxY = Integer.MAX_VALUE;
    /** Location offset x. */
    private int offX;
    /** Location offset y. */
    private int offY;

    /**
     * Create a cursor.
     */
    public Cursor()
    {
        super();
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
        renderer.addImage(id, media);
    }

    /**
     * Set the input device pointer to use.
     * 
     * @param pointer The pointer reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid pointer.
     */
    public void setInputDevice(InputDevicePointer pointer)
    {
        Check.notNull(pointer);
        this.pointer = pointer;
    }

    /**
     * Set the viewer reference.
     * 
     * @param viewer The viewer reference.
     * @throws LionEngineException If invalid viewer.
     */
    public void setViewer(Viewer viewer)
    {
        Check.notNull(pointer);
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
     * @param sh The horizontal sensibility (superior or equal to 0.0).
     * @param sv The vertical sensibility (superior or equal to 0.0).
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
        screenX = UtilMath.clamp(x, minX, maxX);
        screenY = UtilMath.clamp(y, minY, maxY);
    }

    /**
     * Set the surface id to render with {@link #render(Graphic)}.
     * 
     * @param surfaceId The surface id number (must be strictly positive).
     * @throws LionEngineException If invalid id value or not found.
     */
    public void setSurfaceId(int surfaceId)
    {
        renderer.setSurfaceId(surfaceId);
    }

    /**
     * Set the rendering offsets value (allows to apply an offset depending of the cursor surface).
     * 
     * @param ox The horizontal offset.
     * @param oy The vertical offset.
     */
    public void setRenderingOffset(int ox, int oy)
    {
        renderer.setRenderingOffset(ox, oy);
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
     * Set the grid size.
     * 
     * @param width The horizontal grid (strictly positive).
     * @param height The vertical grid (strictly positive).
     * @throws LionEngineException If grid is not strictly positive.
     */
    public void setGrid(int width, int height)
    {
        Check.superiorStrict(width, 0);
        Check.superiorStrict(height, 0);
        gridWidth = width;
        gridHeight = height;
    }

    /**
     * Set the visibility.
     * 
     * @param visible <code>true</code> to show, <code>false</code> to hide.
     */
    public void setVisible(boolean visible)
    {
        renderer.setVisible(visible);
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
    public Integer getSurfaceId()
    {
        return renderer.getSurfaceId();
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
        return screenX;
    }

    /**
     * Get the current vertical location on screen.
     * 
     * @return The current vertical location on screen.
     */
    public double getScreenY()
    {
        return screenY;
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
     * Resource
     */

    @Override
    public void load()
    {
        renderer.load();
    }

    @Override
    public boolean isLoaded()
    {
        return renderer.isLoaded();
    }

    @Override
    public void dispose()
    {
        renderer.dispose();
    }

    /*
     * Updatable
     */

    @Override
    public void update(double extrp)
    {
        if (pointer != null)
        {
            if (sync)
            {
                screenX = pointer.getX();
                screenY = pointer.getY();
            }
            else
            {
                screenX += pointer.getMoveX() * sensibilityHorizontal * extrp;
                screenY += pointer.getMoveY() * sensibilityVertical * extrp;
            }
        }
        if (viewer != null)
        {
            offX = (int) viewer.getX();
            offY = (int) viewer.getY() - viewer.getViewY();
        }

        screenX = UtilMath.clamp(screenX, minX, maxX);
        screenY = UtilMath.clamp(screenY, minY, maxY);
        x = screenX + offX;
        if (viewer != null)
        {
            y = viewer.getHeight() - screenY + offY;
        }
        else
        {
            y = screenY + offY;
        }
        renderer.update(screenX, screenY);
    }

    /*
     * Renderable
     */

    @Override
    public void render(Graphic g)
    {
        renderer.render(g);
    }

    /*
     * Localizable
     */

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
        return gridWidth;
    }

    @Override
    public int getHeight()
    {
        return gridHeight;
    }
}
