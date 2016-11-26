/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.camera;

import com.b3dgs.lionengine.Localizable;
import com.b3dgs.lionengine.Shape;
import com.b3dgs.lionengine.Surface;
import com.b3dgs.lionengine.game.Mover;
import com.b3dgs.lionengine.game.MoverModel;
import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.graphic.Viewer;
import com.b3dgs.lionengine.util.UtilMath;

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
 * <li>{@link #setView(int, int, int, int, int)}</li>
 * <li>{@link #setLimits(Surface)}</li>
 * </ul>
 */
public class Camera extends FeaturableModel implements Viewer
{
    /** Current location. */
    private final Mover mover = new MoverModel();
    /** Current offset location. */
    private final Mover offset = new MoverModel();
    /** Intervals horizontal value. */
    private int intervalHorizontal;
    /** Intervals vertical value. */
    private int intervalVertical;
    /** Camera view location x. */
    private int x;
    /** Camera view location y. */
    private int y;
    /** Camera view width. */
    private int width = Integer.MAX_VALUE;
    /** Camera view height. */
    private int height = Integer.MAX_VALUE;
    /** Limit left. */
    private int limitLeft = Integer.MIN_VALUE;
    /** Limit right. */
    private int limitRight = Integer.MAX_VALUE;
    /** Limit top. */
    private int limitTop = Integer.MAX_VALUE;
    /** Limit bottom. */
    private int limitBottom = Integer.MIN_VALUE;
    /** Screen height. */
    private int screenHeight;

    /**
     * Create a camera.
     */
    public Camera()
    {
        super();
    }

    /**
     * Reset the camera interval to 0 by adapting its position. This will ensure camera centers its view to the
     * localizable.
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
        setLocation(localizable.getX(), localizable.getY());

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
        checkHorizontalLimit(extrp, vx);
        checkVerticalLimit(extrp, vy);
    }

    /**
     * Set the camera location.
     * 
     * @param x The horizontal location.
     * @param y The vertical location.
     */
    public void setLocation(double x, double y)
    {
        final double dx = x - (mover.getX() + offset.getX());
        final double dy = y - (mover.getY() + offset.getY());
        moveLocation(1, dx, dy);
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
        mover.teleport(x, y);
    }

    /**
     * This represents the real position, between -interval and +interval. In other words, camera will move only when
     * the interval location is on its extremity.
     * <p>
     * For example: if the camera is following an object and the camera horizontal interval is 16, anything that is
     * rendered using the camera view point will see its horizontal axis change when the object horizontal location will
     * be before / after the camera location -16 / +16:
     * </p>
     * <ul>
     * <li><code>&lt;--camera movement--&gt; -16[..no camera movement..]+16 &lt;--camera movement--&gt;</code></li>
     * </ul>
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
     * Define the rendering area. Useful to apply an offset during rendering, in order to avoid hiding part.
     * <p>
     * For example:
     * </p>
     * <ul>
     * <li>If the view set is <code>(0, 0, 320, 240)</code>, and the tile size is <code>16</code>, then
     * <code>20</code> horizontal tiles and <code>15</code> vertical tiles will be rendered from <code>0, 0</code>
     * (screen top-left).</li>
     * <li>If the view set is <code>(64, 64, 240, 160)</code>, and the tile size is <code>16</code>, then
     * <code>15</code> horizontal tiles and <code>10</code> vertical tiles will be rendered from <code>64, 64</code>
     * (screen top-left).</li>
     * </ul>
     * <p>
     * It is also compatible with object rendering (by using an {@link com.b3dgs.lionengine.game.handler.Handler}). The
     * object which is outside the camera view will not be rendered. This avoid useless rendering.
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
     * @param screenHeight The screen height.
     */
    public void setView(int x, int y, int width, int height, int screenHeight)
    {
        this.x = x;
        this.y = y;
        this.width = UtilMath.clamp(width, 0, Integer.MAX_VALUE);
        this.height = UtilMath.clamp(height, 0, Integer.MAX_VALUE);
        this.screenHeight = screenHeight;
    }

    /**
     * Define the maximum view limit. This function will allow to let the camera know the max rendering size, and so,
     * know which part can be viewed without being outside the extremity.
     * <p>
     * Note: Must be called after set view ({@link #setView(int, int, int, int, int)}).
     * </p>
     * 
     * @param surface The surface reference.
     */
    public void setLimits(Surface surface)
    {
        limitRight = Math.max(0, surface.getWidth() - width);
        limitLeft = 0;
        limitTop = Math.max(0, surface.getHeight() - height);
        limitBottom = 0;
        moveLocation(1.0, 0.0, 0.0);
    }

    /**
     * Get the horizontal movement.
     * 
     * @return Camera horizontal movement.
     */
    public double getMovementHorizontal()
    {
        return mover.getX() - mover.getOldX();
    }

    /**
     * Get the horizontal movement.
     * 
     * @return Camera horizontal movement.
     */
    public double getMovementVertical()
    {
        return mover.getY() - mover.getOldY();
    }

    /**
     * Check horizontal limit on move.
     * 
     * @param extrp The extrapolation value.
     * @param vx The horizontal movement.
     */
    private void checkHorizontalLimit(double extrp, double vx)
    {
        // Inside interval
        if (mover.getX() >= limitLeft
            && mover.getX() <= limitRight
            && limitLeft != Integer.MIN_VALUE
            && limitRight != Integer.MAX_VALUE)
        {
            offset.moveLocation(extrp, vx, 0);

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
        if ((int) offset.getX() == -intervalHorizontal || (int) offset.getX() == intervalHorizontal)
        {
            mover.moveLocationX(extrp, vx);
        }
        applyHorizontalLimit();
    }

    /**
     * Check vertical limit on move.
     * 
     * @param extrp The extrapolation value.
     * @param vy The vertical movement.
     */
    private void checkVerticalLimit(double extrp, double vy)
    {
        // Inside interval
        if (mover.getY() >= limitBottom
            && mover.getY() <= limitTop
            && limitBottom != Integer.MIN_VALUE
            && limitTop != Integer.MAX_VALUE)
        {
            offset.moveLocation(extrp, 0, vy);

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
        if ((int) offset.getY() == -intervalVertical || (int) offset.getY() == intervalVertical)
        {
            mover.moveLocationY(extrp, vy);
        }
        applyVerticalLimit();
    }

    /**
     * Fix location inside horizontal limit.
     */
    private void applyHorizontalLimit()
    {
        if (mover.getX() < limitLeft && limitLeft != Integer.MIN_VALUE)
        {
            mover.teleportX(limitLeft);
        }
        else if (mover.getX() > limitRight && limitRight != Integer.MAX_VALUE)
        {
            mover.teleportX(limitRight);
        }
    }

    /**
     * Fix location inside vertical limit.
     */
    private void applyVerticalLimit()
    {
        if (mover.getY() < limitBottom && limitBottom != Integer.MIN_VALUE)
        {
            mover.teleportY(limitBottom);
        }
        else if (mover.getY() > limitTop && limitTop != Integer.MAX_VALUE)
        {
            mover.teleportY(limitTop);
        }
    }

    /*
     * Viewer
     */

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
        return mover.getX() - getViewX();
    }

    @Override
    public double getY()
    {
        return mover.getY() + getViewY();
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
    public int getScreenHeight()
    {
        return screenHeight;
    }

    @Override
    public boolean isViewable(Localizable localizable, int radiusX, int radiusY)
    {
        return getViewpointX(localizable.getX() + radiusX) >= getViewX()
               && getViewpointX(localizable.getX() - radiusX) <= getViewX() + width
               && getViewpointY(localizable.getY() - radiusY) >= getViewY()
               && getViewpointY(localizable.getY() + radiusY) <= getViewY() + height;
    }

    @Override
    public boolean isViewable(Shape shape, int radiusX, int radiusY)
    {
        return getViewpointX(shape.getX() + shape.getWidth() + radiusX) >= getViewX()
               && getViewpointX(shape.getX() - shape.getWidth() - radiusX) <= getViewX() + width
               && getViewpointY(shape.getY() - shape.getHeight() - radiusY) >= getViewY()
               && getViewpointY(shape.getY() + shape.getHeight() + radiusY) <= getViewY() + height;
    }
}
