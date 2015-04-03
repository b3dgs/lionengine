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

import com.b3dgs.lionengine.Localizable;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.object.Handler;
import com.b3dgs.lionengine.game.trait.transformable.Transformable;
import com.b3dgs.lionengine.game.trait.transformable.TransformableModel;

/**
 * Standard camera, able to handle movement, and both vertical/horizontal interval. Camera can be used to move
 * easily, or just follow a specific {@link Localizable}. Also, a view can be set to avoid useless rendering when
 * objects are outside of the camera view.
 * <p>
 * Camera construction order example:
 * </p>
 * <ul>
 * <li>{@link #Camera()}</li>
 * <li>{@link #setIntervals(int, int)}</li>
 * <li>{@link #setView(int, int, int, int)}</li>
 * <li>{@link #setLimits(MapTile)}</li>
 * </ul>
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class Camera
        implements Viewer
{
    /** Current location. */
    private final Transformable transformable;
    /** Current offset location. */
    private final Transformable offset;
    /** Intervals horizontal value. */
    private int intervalHorizontal;
    /** Intervals vertical value. */
    private int intervalVertical;
    /** Camera view location x. */
    private int x;
    /** Camera view location y. */
    private int y;
    /** Camera view width. */
    private int width;
    /** Camera view height. */
    private int height;
    /** Map limits left. */
    private int mapLeftLimit;
    /** Map limits right. */
    private int mapRightLimit;
    /** Map limits up. */
    private int mapUpLimit;
    /** Map limits down. */
    private int mapDownLimit;

    /**
     * Create a camera.
     */
    public Camera()
    {
        transformable = new TransformableModel();
        offset = new TransformableModel();
        intervalHorizontal = 0;
        intervalVertical = 0;
        x = 0;
        y = 0;
        width = 0;
        height = 0;
        mapLeftLimit = Integer.MIN_VALUE;
        mapRightLimit = Integer.MAX_VALUE;
        mapUpLimit = Integer.MAX_VALUE;
        mapDownLimit = Integer.MIN_VALUE;
    }

    /**
     * Reset the camera interval to 0 by adapting its position.
     * 
     * @param localizable The localizable to center to.
     */
    public void resetInterval(Localizable localizable)
    {
        final int intervalHorizontalOld = intervalHorizontal;
        final int intervalVerticalOld = intervalVertical;
        final double oldX = getX();
        final double oldY = getY();

        setIntervals(0, 0);
        offset.setLocation(0.0, 0.0);
        follow(localizable);

        final double newX = getX();
        final double newY = getY();

        moveLocation(1.0, oldX - newX, oldY - newY);
        moveLocation(1.0, newX - oldX, newY - oldY);

        setIntervals(intervalHorizontalOld, intervalVerticalOld);
        offset.setLocation(0.0, 0.0);
    }

    /**
     * Move camera by using specified vector.
     * 
     * @param extrp The extrapolation value.
     * @param vx The horizontal vector.
     * @param vy The vertical vector.
     */
    public void moveLocation(double extrp, double vx, double vy)
    {
        checkHorizontalLimit(vx);
        checkVerticalLimit(vy);
    }

    /**
     * Set the camera location.
     * 
     * @param x The horizontal location.
     * @param y The vertical location.
     */
    public void setLocation(double x, double y)
    {
        moveLocation(1, x - width / 2.0 - (transformable.getX() + offset.getX()),
                y - height / 2.0 - (transformable.getY() + offset.getY()));
    }

    /**
     * Teleport the camera at the specified location and reset offset position.
     * 
     * @param x The horizontal location.
     * @param y The vertical location.
     */
    public void teleport(double x, double y)
    {
        offset.teleport(0, 0);
        transformable.teleport(x, y);
    }

    /**
     * This represents the real position, between -interval and +interval. In other words, camera will move only when
     * the interval location is on its extremity.
     * <p>
     * For example: if the camera is following an object and the camera horizontal interval is 16, anything that is
     * rendered using the camera view point will see its horizontal axis change when the object horizontal location will
     * be before / after the camera location -16 / +16:
     * <ul>
     * <li><code><--camera movement--> -16[..no camera movement..]+16 <--camera movement--></code></li>
     * </ul>
     * </p>
     * 
     * @param intervalHorizontal The horizontal margin.
     * @param intervalVertical The vertical margin.
     */
    public void setIntervals(int intervalHorizontal, int intervalVertical)
    {
        this.intervalHorizontal = intervalHorizontal;
        this.intervalVertical = intervalVertical;
    }

    /**
     * Define the rendering area. Useful to apply an offset during map rendering, in order to avoid hiding map part.
     * <p>
     * For example:
     * </p>
     * <ul>
     * <li>If the view set is <code>(0, 0, 320, 240)</code>, and the map tile size is <code>16</code>, then
     * <code>20</code> horizontal tiles and <code>15</code> vertical tiles will be rendered from <code>0, 0</code>
     * (screen top-left).</li>
     * <li>If the view set is <code>(64, 64, 240, 160)</code>, and the map tile size is <code>16</code>, then
     * <code>15</code> horizontal tiles and <code>10</code> vertical tiles will be rendered from <code>64, 64</code>
     * (screen top-left).</li>
     * </ul>
     * <p>
     * It is also compatible with object rendering (by using an {@link Handler}). The object which is outside the camera
     * view will not be rendered. This avoid useless rendering.
     * </p>
     * <p>
     * Note: The rendering view is from the camera location. So <code>x</code> and <code>y</code> are an offset from
     * this location.
     * </p>
     * 
     * @param x The horizontal offset.
     * @param y The vertical offset.
     * @param width The rendering width (positive value).
     * @param height The rendering height (positive value).
     */
    public void setView(int x, int y, int width, int height)
    {
        this.x = x;
        this.y = y;
        this.width = UtilMath.fixBetween(width, 0, Integer.MAX_VALUE);
        this.height = UtilMath.fixBetween(height, 0, Integer.MAX_VALUE);
    }

    /**
     * Define the map borders. This function will allow to let the camera know the map size, and so, know which part of
     * the map can be viewed without being outside the map extremity.
     * <p>
     * Note: Must be called after set view ({@link #setView(int, int, int, int)}).
     * </p>
     * 
     * @param map The map reference.
     */
    public void setLimits(MapTile map)
    {
        mapUpLimit = UtilMath.fixBetween(map.getInTileHeight() * map.getTileHeight() - height, 0, Integer.MAX_VALUE);
        mapLeftLimit = 0;
        mapRightLimit = map.getInTileWidth() * map.getTileWidth() - width;
        mapDownLimit = 0;
        moveLocation(1.0, 0.0, 0.0);
    }

    /**
     * Check horizontal limit on move.
     * 
     * @param vx The horizontal movement.
     */
    private void checkHorizontalLimit(double vx)
    {
        // Inside interval
        if (transformable.getX() >= mapLeftLimit && transformable.getX() <= mapRightLimit
                && mapLeftLimit != Integer.MIN_VALUE && mapRightLimit != Integer.MAX_VALUE)
        {
            offset.moveLocation(1, vx, 0);

            // Block offset on its limits
            if (offset.getX() < -intervalHorizontal)
            {
                offset.teleportX(-intervalHorizontal);
            }
            else if (offset.getX() > intervalHorizontal)
            {
                offset.teleportX(intervalHorizontal);
            }
        }
        // Outside interval
        if (offset.getX() == -intervalHorizontal || offset.getX() == intervalHorizontal)
        {
            transformable.moveLocation(1, vx, 0);
        }
        // Apply limit
        if (transformable.getX() < mapLeftLimit && mapLeftLimit != Integer.MIN_VALUE)
        {
            transformable.teleportX(mapLeftLimit);
        }
        else if (transformable.getX() > mapRightLimit && mapRightLimit != Integer.MAX_VALUE)
        {
            transformable.teleportX(mapRightLimit);
        }
        else
        {
            transformable.moveLocation(1, Direction.ZERO);
        }
    }

    /**
     * Check vertical limit on move.
     * 
     * @param vy The vertical movement.
     */
    private void checkVerticalLimit(double vy)
    {
        // Inside interval
        if (transformable.getY() >= mapDownLimit && transformable.getY() <= mapUpLimit
                && mapDownLimit != Integer.MIN_VALUE && mapUpLimit != Integer.MAX_VALUE)
        {
            offset.moveLocation(1, 0, vy);

            // Block offset on its limits
            if (offset.getY() < -intervalVertical)
            {
                offset.teleportY(-intervalVertical);
            }
            else if (offset.getY() > intervalVertical)
            {
                offset.teleportY(intervalVertical);
            }
        }
        // Outside interval
        if (offset.getY() == -intervalVertical || offset.getY() == intervalVertical)
        {
            transformable.moveLocation(1, 0, vy);
        }
        // Apply limit
        if (transformable.getY() < mapDownLimit && mapDownLimit != Integer.MIN_VALUE)
        {
            transformable.teleportY(mapDownLimit);
        }
        else if (transformable.getY() > mapUpLimit && mapUpLimit != Integer.MAX_VALUE)
        {
            transformable.teleportY(mapUpLimit);
        }
        else
        {
            transformable.moveLocation(1, Direction.ZERO);
        }
    }

    /*
     * Viewer
     */

    @Override
    public void follow(Localizable localizable)
    {
        setLocation(localizable.getX(), localizable.getY());
    }

    @Override
    public double getViewpointX(double x)
    {
        return x - getX();
    }

    @Override
    public double getViewpointY(double y)
    {
        return getY() + height - y;
    }

    @Override
    public double getX()
    {
        return transformable.getX() - getViewX();
    }

    @Override
    public double getY()
    {
        return transformable.getY() + getViewY();
    }

    @Override
    public int getViewX()
    {
        return x;
    }

    @Override
    public int getViewY()
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
    public boolean isViewable(Localizable localizable, int radiusX, int radiusY)
    {
        return localizable.getX() + localizable.getWidth() + radiusX >= getX()
                && localizable.getX() - localizable.getWidth() - radiusX <= getX() + width
                && localizable.getY() + localizable.getHeight() + radiusY >= getY()
                && localizable.getY() - localizable.getHeight() * 2 - radiusY <= getY() + height;
    }
}
