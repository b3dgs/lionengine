/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Localizable;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Shape;
import com.b3dgs.lionengine.Surface;
import com.b3dgs.lionengine.SurfaceTile;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.game.Mover;
import com.b3dgs.lionengine.game.MoverModel;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionProvider;

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
public class Camera extends FeaturableAbstract implements Viewer
{
    private static final int SPLIT_MAX = 4;

    /** Internal split. */
    private final Camera[] internal = new Camera[SPLIT_MAX];
    /** Current location. */
    private final Mover mover = new MoverModel();
    /** Current offset location. */
    private final Mover offset = new MoverModel();
    /** Internal split index. */
    private Camera split;
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
    /** Shake x. */
    private int shakeX;
    /** Shake y. */
    private int shakeY;
    /** Shake 2 x. */
    private int shake2X;
    /** Shake 2 y. */
    private int shake2Y;

    /**
     * Create a camera.
     */
    public Camera()
    {
        super();

        split = this;
        internal[0] = this;
    }

    /**
     * Set active split.
     * 
     * @param split The active split (0 default).
     */
    public void setSplit(int split)
    {
        if (split < 0 || split > internal.length || internal[split] == null)
        {
            this.split = this;
        }
        else
        {
            this.split = internal[split];
        }
    }

    /**
     * Define internal camera for split.
     * 
     * @param split The split index (between 1 and 4 included).
     * @param camera The camera reference.
     * @throws LionEngineException If invalid argument.
     */
    public void setInternal(int split, Camera camera)
    {
        Check.superiorStrict(split, 0);
        Check.inferiorOrEqual(split, internal.length);

        internal[split] = camera;
    }

    /**
     * Backup current location.
     */
    public void backup()
    {
        split.mover.backup();
    }

    /**
     * Reset the camera interval to 0 by adapting its position. This will ensure camera centers its view to the
     * localizable.
     * 
     * @param localizable The localizable to center to.
     */
    public void resetInterval(Localizable localizable)
    {
        final int intervalHorizontalOld = split.intervalHorizontal;
        final int intervalVerticalOld = split.intervalVertical;
        final double oldX = getX();
        final double oldY = getY();

        setIntervals(0, 0);
        split.offset.setLocation(0.0, 0.0);
        setLocation(localizable.getX(), localizable.getY());

        final double newX = getX();
        final double newY = getY();

        moveLocation(1.0, oldX - newX, oldY - newY);
        moveLocation(1.0, newX - oldX, newY - oldY);

        setIntervals(intervalHorizontalOld, intervalVerticalOld);
        split.offset.setLocation(0.0, 0.0);
        split.mover.backup();
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
        final double dx = x - (split.mover.getX() + split.offset.getX());
        final double dy = y - (split.mover.getY() + split.offset.getY());
        moveLocation(1.0, dx, dy);
    }

    /**
     * Set the camera location horizontal.
     * 
     * @param x The horizontal location.
     */
    public void setLocationX(double x)
    {
        final double dx = x - (split.mover.getX() + split.offset.getX());
        checkHorizontalLimit(1.0, dx);
    }

    /**
     * Set the camera location vertical.
     * 
     * @param y The vertical location.
     */
    public void setLocationY(double y)
    {
        final double dy = y - (split.mover.getY() + split.offset.getY());
        checkVerticalLimit(1.0, dy);
    }

    /**
     * Teleport the camera at the specified location and reset offset position.
     * 
     * @param x The horizontal location.
     * @param y The vertical location.
     */
    public void teleport(double x, double y)
    {
        split.offset.teleport(0.0, 0.0);
        split.mover.teleport(x, y);
    }

    /**
     * Teleport the camera at the specified location and reset offset position.
     * 
     * @param shape The center to center camera on.
     */
    public void center(Shape shape)
    {
        teleport(shape.getX() + (shape.getWidth() - getWidth()) / 2.0,
                 shape.getY() + (shape.getHeight() - getHeight()) / 2.0);
    }

    /**
     * Round location with the specified size.
     * 
     * @param round The round used.
     */
    public void round(SurfaceTile round)
    {
        final int th = round.getTileHeight();
        teleport(UtilMath.getRounded(split.mover.getX(), round.getTileWidth()),
                 UtilMath.getRounded(split.mover.getY(), th) + (double) th);
    }

    /**
     * Draw the camera field of view according to a grid.
     * 
     * @param g The graphic output.
     * @param x The horizontal location.
     * @param y The vertical location.
     * @param gridH The horizontal grid.
     * @param gridV The vertical grid.
     * @param surface The surface referential (minimap).
     */
    public void drawFov(Graphic g, int x, int y, int gridH, int gridV, Surface surface)
    {
        final int h = x + UtilMath.getRounded(getX() + getViewX(), gridH) / gridH;
        final int v = y + -UtilMath.getRounded(getY() + getHeight(), gridV) / gridV;
        final int tileWidth = UtilMath.getRounded(getWidth(), gridH) / gridH;
        final int tileHeight = UtilMath.getRounded(getHeight(), gridV) / gridV;
        g.drawRect(h, v + surface.getHeight(), tileWidth - 1, tileHeight - 1, false);
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
        split.intervalHorizontal = intervalHorizontal;
        split.intervalVertical = intervalVertical;
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
     * It is also compatible with object rendering (by using an {@link com.b3dgs.lionengine.game.feature.Handler}). The
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
        split.x = x;
        split.y = y;
        split.width = UtilMath.clamp(width, 0, Integer.MAX_VALUE);
        split.height = UtilMath.clamp(height, 0, Integer.MAX_VALUE);
        split.screenHeight = screenHeight;
    }

    /**
     * Define the rendering area.
     * 
     * @param source The source resolution (must not be <code>null</code>).
     * @param offsetX The horizontal offset.
     * @param offsetY The vertical offset.
     * @param origin The view origin.
     * @throws LionEngineException If invalid argument.
     * @see #setView(int, int, int, int, int)
     */
    public void setView(SourceResolutionProvider source, int offsetX, int offsetY, Origin origin)
    {
        Check.notNull(source);
        Check.notNull(origin);

        final int sourceWidth = source.getWidth();
        final int sourceHeight = source.getHeight();
        setView((int) origin.getX(offsetX, sourceWidth),
                (int) origin.getY(offsetY, sourceHeight),
                sourceWidth - offsetX,
                sourceHeight - offsetY,
                sourceHeight);
    }

    /**
     * Set the shake effect.
     * 
     * @param shakeX The horizontal shake.
     * @param shakeY The vertical shake.
     */
    public void setShake(int shakeX, int shakeY)
    {
        split.shakeX = shakeX;
        split.shakeY = shakeY;
    }

    /**
     * Set the shake 2 effect.
     * 
     * @param shake2X The horizontal shake.
     * @param shake2Y The vertical shake.
     */
    public void setShake2(int shake2X, int shake2Y)
    {
        split.shake2X = shake2X;
        split.shake2Y = shake2Y;
    }

    /**
     * Define the maximum view limit. This function will allow to let the camera know the max rendering size, and so,
     * know which part can be viewed without being outside the extremity.
     * <p>
     * Note: Must be called after set view ({@link #setView(int, int, int, int, int)}).
     * </p>
     * 
     * @param surface The surface reference.
     * @throws LionEngineException If <code>null</code> surface.
     */
    public void setLimits(Surface surface)
    {
        setLimits(surface, 1, 1);
    }

    /**
     * Define the maximum view limit. This function will allow to let the camera know the max rendering size, and so,
     * know which part can be viewed without being outside the extremity.
     * <p>
     * Note: Must be called after set view ({@link #setView(int, int, int, int, int)}).
     * </p>
     * 
     * @param surface The surface reference.
     * @throws LionEngineException If <code>null</code> surface.
     */
    public void setLimits(SurfaceTile surface)
    {
        setLimits(surface, surface.getTileWidth(), surface.getTileHeight());
    }

    /**
     * Set the left limit.
     * 
     * @param limitLeft The left limit.
     */
    public void setLimitLeft(int limitLeft)
    {
        split.limitLeft = limitLeft;
    }

    /**
     * Set the right limit.
     * 
     * @param limitRight The right limit.
     */
    public void setLimitRight(int limitRight)
    {
        split.limitRight = limitRight;
    }

    /**
     * Set the top limit.
     * 
     * @param limitTop The top limit.
     */
    public void setLimitTop(int limitTop)
    {
        split.limitTop = limitTop;
    }

    /**
     * Set the bottom limit.
     * 
     * @param limitBottom The bottom limit.
     */
    public void setLimitBottom(int limitBottom)
    {
        split.limitBottom = limitBottom;
    }

    /**
     * Define the maximum view limit.
     * 
     * @param surface The surface reference.
     * @param gridH The horizontal grid.
     * @param gridV The vertical grid.
     * @throws LionEngineException If <code>null</code> surface.
     */
    private void setLimits(Surface surface, int gridH, int gridV)
    {
        Check.notNull(surface);

        if (gridH == 0)
        {
            split.limitRight = 0;
        }
        else
        {
            split.limitRight = Math.max(0, surface.getWidth() - UtilMath.getRoundedC(split.width, gridH));
        }
        if (gridV == 0)
        {
            split.limitTop = 0;
        }
        else
        {
            split.limitTop = Math.max(0, surface.getHeight() - UtilMath.getRoundedC(split.height, gridV));
        }
        split.limitLeft = 0;
        split.limitBottom = 0;

        moveLocation(1.0, 0.0, 0.0);
    }

    /**
     * Get the horizontal movement.
     * 
     * @return Camera horizontal movement.
     */
    public double getMovementHorizontal()
    {
        return split.mover.getX() - split.mover.getOldX();
    }

    /**
     * Get the horizontal movement.
     * 
     * @return Camera horizontal movement.
     */
    public double getMovementVertical()
    {
        return split.mover.getY() - split.mover.getOldY();
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
        if (split.mover.getX() >= split.limitLeft
            && split.mover.getX() <= split.limitRight
            && split.limitLeft != Integer.MIN_VALUE
            && split.limitRight != Integer.MAX_VALUE)
        {
            split.offset.moveLocation(extrp, vx, 0);

            // Block offset on its limits
            if (split.offset.getX() < -split.intervalHorizontal)
            {
                split.offset.teleportX(-split.intervalHorizontal);
            }
            else if (split.offset.getX() > split.intervalHorizontal)
            {
                split.offset.teleportX(split.intervalHorizontal);
            }
        }
        // Outside interval
        if ((int) split.offset.getX() == -split.intervalHorizontal
            || (int) split.offset.getX() == split.intervalHorizontal)
        {
            split.mover.moveLocationX(extrp, vx);
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
        if (split.mover.getY() >= split.limitBottom
            && split.mover.getY() <= split.limitTop
            && split.limitBottom != Integer.MIN_VALUE
            && split.limitTop != Integer.MAX_VALUE)
        {
            split.offset.moveLocation(extrp, 0, vy);

            // Block offset on its limits
            if (split.offset.getY() < -split.intervalVertical)
            {
                split.offset.teleportY(-split.intervalVertical);
            }
            else if (split.offset.getY() > split.intervalVertical)
            {
                split.offset.teleportY(split.intervalVertical);
            }
        }
        // Outside interval
        if ((int) offset.getY() == -split.intervalVertical || (int) split.offset.getY() == split.intervalVertical)
        {
            split.mover.moveLocationY(extrp, vy);
        }
        applyVerticalLimit();
    }

    /**
     * Fix location inside horizontal limit.
     */
    private void applyHorizontalLimit()
    {
        if (split.mover.getX() < split.limitLeft && split.limitLeft != Integer.MIN_VALUE)
        {
            split.mover.teleportX(limitLeft);
        }
        else if (split.mover.getX() > split.limitRight && split.limitRight != Integer.MAX_VALUE)
        {
            split.mover.teleportX(split.limitRight);
        }
    }

    /**
     * Fix location inside vertical limit.
     */
    private void applyVerticalLimit()
    {
        if (split.mover.getY() < split.limitBottom && split.limitBottom != Integer.MIN_VALUE)
        {
            split.mover.teleportY(split.limitBottom);
        }
        else if (split.mover.getY() > split.limitTop && split.limitTop != Integer.MAX_VALUE)
        {
            split.mover.teleportY(split.limitTop);
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
        return getY() + split.height - y;
    }

    @Override
    public double getX()
    {
        return split.mover.getX() - getViewX() + split.shakeX + split.shake2X;
    }

    @Override
    public double getY()
    {
        return split.mover.getY() + getViewY() + split.shakeY + split.shake2Y;
    }

    @Override
    public int getViewX()
    {
        return split.x;
    }

    @Override
    public int getViewY()
    {
        return split.y;
    }

    @Override
    public int getWidth()
    {
        return split.width;
    }

    @Override
    public int getHeight()
    {
        return split.height;
    }

    @Override
    public int getScreenHeight()
    {
        return split.screenHeight;
    }

    @Override
    public boolean isViewable(Localizable localizable, int radiusX, int radiusY)
    {
        for (int i = 0; i < internal.length; i++)
        {
            if (internal[i] != null)
            {
                final boolean outside = getViewpointX(localizable.getX() + radiusX) < getViewX()
                                        || getViewpointX(localizable.getX() - radiusX) > getViewX() + internal[i].width
                                        || getViewpointY(localizable.getY() - radiusY) < getViewY()
                                        || getViewpointY(localizable.getY() + radiusY) > getViewY()
                                                                                         + internal[i].height;
                if (!outside)
                {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean isViewable(Shape shape, int radiusX, int radiusY)
    {
        for (int i = 0; i < internal.length; i++)
        {
            if (internal[i] != null)
            {
                final boolean outside = getViewpointX(shape.getX() + shape.getWidth() + radiusX) < getViewX()
                                        || getViewpointX(shape.getX()
                                                         - shape.getWidth()
                                                         - radiusX) > getViewX() + internal[i].width
                                        || getViewpointY(shape.getY() - shape.getHeight() - radiusY) < getViewY()
                                        || getViewpointY(shape.getY()
                                                         + shape.getHeight()
                                                         + radiusY) > getViewY() + internal[i].height;
                if (!outside)
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Return always <code>null</code>.
     */
    @Override
    public Media getMedia()
    {
        return null;
    }
}
