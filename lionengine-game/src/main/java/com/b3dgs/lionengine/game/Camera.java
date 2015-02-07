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

import com.b3dgs.lionengine.Localizable;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.game.handler.Handler;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.trait.Transformable;
import com.b3dgs.lionengine.game.trait.TransformableModel;
import com.b3dgs.lionengine.stream.FileReading;

/**
 * Standard camera, able to handle movement, and both vertical/horizontal real interval. Camera can be used to move
 * easily, or just follow a specific {@link Localizable}. Also, a view can be set to avoid useless rendering when
 * objects are outside of the camera view.
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
        transformable = new TransformableModel(null);
        offset = new TransformableModel(null);
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
     * Move camera.
     * 
     * @param extrp The extrapolation value.
     * @param vx The horizontal vector.
     * @param vy The vertical vector.
     */
    public void moveLocation(double extrp, double vx, double vy)
    {
        // Horizontal move
        // Can scroll only on offset interval
        checkHorizontalLimit(vx);

        // Middle
        if (y >= getLimitMapDown() && y < getLimitMapUp())
        {
            transformable.setLocationY(y);
            offset.setLocationY(0.0);
        }
        else
        {
            // Vertical move
            if (offset.getY() == 0)
            {
                transformable.moveLocation(extrp, 0, vy);
            }
            if (transformable.getY() > getLimitMapDown() && transformable.getY() < getLimitMapUp())
            {
                offset.setLocationY(0.0);
            }
            else
            {
                if (transformable.getY() < getLimitMapDown())
                {
                    transformable.setLocationY(getLimitMapDown());
                }
                if (transformable.getY() > getLimitMapUp())
                {
                    transformable.setLocationY(getLimitMapUp());
                }

                offset.moveLocation(extrp, 0, vy);

                if (transformable.getY() == getLimitMapDown() && offset.getY() >= 0)
                {
                    transformable.moveLocation(extrp, 0, vy);
                    offset.setLocationY(0.0);
                }
                if (transformable.getY() == getLimitMapUp() && offset.getY() <= 0)
                {
                    transformable.moveLocation(extrp, 0, vy);
                    offset.setLocationY(0.0);
                }
            }
            // Down limit
            if (y < getLimitMapDown())
            {
                transformable.setLocationY(getLimitMapDown());
                offset.setLocationY(y - transformable.getY());
            }
            // Top limit
            if (y >= getLimitMapUp())
            {
                transformable.setLocationY(getLimitMapUp());
                offset.setLocationY(y - transformable.getY());
            }
        }
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
     * This represents the real position, between -interval and +interval. In other words, camera will move only when
     * the interval location is on its extremity.
     * <p>
     * For example: if the camera is following an entity and the camera horizontal interval is 16, anything that is
     * rendered using the camera view point will see its horizontal axis change when the entity horizontal location will
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
     * It is also compatible with entity rendering (by using an {@link Handler}). The entity which are outside the
     * camera view will not be rendered. This avoid useless rendering.
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
     * Note: Must be called after map loading (usually in {@link WorldGame#loading(FileReading)}).
     * </p>
     * 
     * @param map The map reference.
     */
    public void setLimits(MapTile<?> map)
    {
        final int tw = map.getTileWidth();
        final int th = map.getTileHeight();
        final int top = UtilMath.fixBetween(map.getHeightInTile() * th - height, 0, Integer.MAX_VALUE);
        final int right = map.getWidthInTile() * tw - width;
        mapUpLimit = top;
        mapLeftLimit = 0;
        mapRightLimit = right;
        mapDownLimit = 0;
        moveLocation(1.0, 0.0, 0.0);
    }

    /**
     * Get map left border.
     * 
     * @return The map left border.
     */
    public int getLimitMapLeft()
    {
        return mapLeftLimit;
    }

    /**
     * Get map right border.
     * 
     * @return The map right border.
     */
    public int getLimitMapRight()
    {
        return mapRightLimit;
    }

    /**
     * Get map up border.
     * 
     * @return The map up border.
     */
    public int getLimitMapUp()
    {
        return mapUpLimit;
    }

    /**
     * Get map down border.
     * 
     * @return The map down border.
     */
    public int getLimitMapDown()
    {
        return mapDownLimit;
    }

    /**
     * Get the horizontal location in tile.
     * 
     * @param map The map reference.
     * @return The horizontal location in tile.
     */
    public int getLocationInTileX(MapTile<?> map)
    {
        return (int) getX() / map.getTileWidth();
    }

    /**
     * Get the vertical location in tile.
     * 
     * @param map The map reference.
     * @return The vertical location in tile.
     */
    public int getLocationInTileY(MapTile<?> map)
    {
        return (int) getY() / map.getTileHeight();
    }

    /**
     * Check horizontal limit on move.
     * 
     * @param vx The horizontal movement.
     */
    private void checkHorizontalLimit(double vx)
    {
        // Middle
        if (offset.getX() == -intervalHorizontal || offset.getX() == intervalHorizontal)
        {
            transformable.moveLocation(1, vx, 0);
        }

        if (transformable.getX() > getLimitMapLeft() && transformable.getX() < getLimitMapRight()
                && getLimitMapLeft() != Integer.MIN_VALUE && getLimitMapRight() != Integer.MAX_VALUE)
        {
            offset.moveLocation(1, vx, 0);

            // Block offset on its limits
            if (offset.getX() < -intervalHorizontal)
            {
                offset.setLocationX(-intervalHorizontal);
            }
            else if (offset.getX() > intervalHorizontal)
            {
                offset.setLocationX(intervalHorizontal);
            }
            else
            {
                transformable.moveLocation(1, Direction.ZERO);
            }
        }
        // Case of map extremity
        else
        {
            checkHorizontalExtremity(vx);
        }
    }

    /**
     * Check horizontal extremity on move.
     * 
     * @param vx The horizontal movement.
     */
    private void checkHorizontalExtremity(double vx)
    {
        if (transformable.getX() < getLimitMapLeft() && getLimitMapLeft() != Integer.MIN_VALUE)
        {
            transformable.setLocationX(getLimitMapLeft());
        }
        else if (transformable.getX() > getLimitMapRight() && getLimitMapRight() != Integer.MAX_VALUE)
        {
            transformable.setLocationX(getLimitMapRight());
        }
        else
        {
            transformable.moveLocation(1, Direction.ZERO);
        }

        offset.moveLocation(1, vx, 0);

        if (transformable.getX() == getLimitMapLeft() && offset.getX() >= intervalHorizontal
                && getLimitMapLeft() != Integer.MIN_VALUE)
        {
            offset.setLocationX(intervalHorizontal);
            transformable.moveLocation(1, vx, 0);
        }
        if (transformable.getX() == getLimitMapRight() && offset.getX() <= -intervalHorizontal
                && getLimitMapRight() != Integer.MAX_VALUE)
        {
            offset.setLocationX(-intervalHorizontal);
            transformable.moveLocation(1, vx, 0);
        }
    }

    /*
     * Viewer
     */

    @Override
    public void follow(Localizable localizable)
    {
        setLocation(localizable.getX(), localizable.getY() + (int) Math.floor(localizable.getHeight() / 2.0));
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
