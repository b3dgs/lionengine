package com.b3dgs.lionengine.game;

import java.awt.geom.Line2D;

import com.b3dgs.lionengine.game.entity.HandlerEntityGame;
import com.b3dgs.lionengine.game.purview.Localizable;
import com.b3dgs.lionengine.game.purview.model.LocalizableModel;
import com.b3dgs.lionengine.utility.UtilityMath;

/**
 * Standard camera, able to handle movement, and both vertical/horizontal real interval. Camera can be used to move
 * easily, or just follow a specific {@link Localizable}. Also, a view can be set to avoid useless rendering when
 * objects are outside of the camera view.
 */
public class CameraGame
        implements Localizable
{
    /** Current location. */
    protected final Localizable location;
    /** Current offset location. */
    protected final Localizable offset;
    /** Intervals horizontal value. */
    protected int intervalHorizontal;
    /** Intervals vertical value. */
    protected int intervalVertical;
    /** Camera view location x. */
    private int x;
    /** Camera view location y. */
    private int y;
    /** Camera view width. */
    private int width;
    /** Camera view height. */
    private int height;

    /**
     * Create a new camera.
     */
    public CameraGame()
    {
        location = new LocalizableModel();
        offset = new LocalizableModel();
        intervalHorizontal = 0;
        intervalVertical = 0;
        x = 0;
        y = 0;
        width = -1;
        height = -1;
    }

    /**
     * Follow automatically the specified localizable. The camera location will be adjusted to the followed localizable.
     * 
     * @param localizable The localizable to follow.
     */
    public void follow(Localizable localizable)
    {
        setLocation(localizable.getLocationX(), localizable.getLocationY());
    }

    /**
     * Reset the camera interval to 0 by adapting its position.
     * 
     * @param localizable The localizable to center.
     */
    public void resetInterval(Localizable localizable)
    {
        final int intervalHorizontalOld = intervalHorizontal;
        final int intervalVerticalOld = intervalVertical;
        final double oldX = getLocationX();
        final double oldY = getLocationY();

        setIntervals(0, 0);
        offset.setLocation(0.0, 0.0);
        follow(localizable);

        final double newX = getLocationX();
        final double newY = getLocationY();

        moveLocation(1.0, oldX - newX, oldY - newY);
        moveLocation(1.0, newX - oldX, newY - oldY);

        setIntervals(intervalHorizontalOld, intervalVerticalOld);
        offset.setLocation(0.0, 0.0);
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
     * It is also compatible with entity rendering (by using an {@link HandlerEntityGame}). The entity which are outside
     * the camera view will not be rendered. This avoid useless rendering.
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
        this.width = UtilityMath.fixBetween(width, 0, Integer.MAX_VALUE);
        this.height = UtilityMath.fixBetween(height, 0, Integer.MAX_VALUE);
    }

    /**
     * Get the horizontal viewpoint from the object location. This is the horizontal point considering the camera as
     * viewer.
     * 
     * @param x The object horizontal location.
     * @return The horizontal viewpoint.
     */
    public int getViewpointX(int x)
    {
        return x - getLocationIntX();
    }

    /**
     * Get the vertical viewpoint from the object location. This is the vertical point considering the camera as viewer.
     * 
     * @param y The object vertical location.
     * @return The vertical viewpoint.
     */
    public int getViewpointY(int y)
    {
        return getLocationIntY() + getViewHeight() - y;
    }

    /**
     * Get horizontal view offset.
     * 
     * @return The horizontal view offset.
     */
    public int getViewX()
    {
        return x;
    }

    /**
     * Get vertical view offset.
     * 
     * @return The vertical view offset.
     */
    public int getViewY()
    {
        return y;
    }

    /**
     * Get view width.
     * 
     * @return The view width.
     */
    public int getViewWidth()
    {
        return width;
    }

    /**
     * Get view height.
     * 
     * @return The view height.
     */
    public int getViewHeight()
    {
        return height;
    }

    /**
     * Get the horizontal movement (current position - old).
     * 
     * @return The horizontal movement.
     */
    public double getMovementHorizontal()
    {
        return location.getLocationX() - location.getLocationOldX();
    }

    /**
     * Get the vertical movement (current position - old).
     * 
     * @return The vertical movement.
     */
    public double getMovementVertical()
    {
        return location.getLocationY() - location.getLocationOldY();
    }

    /**
     * Get real horizontal location (relative location + offset location).
     * 
     * @return The real horizontal location
     */
    public double getLocationRealX()
    {
        return location.getLocationX() + offset.getLocationX();
    }

    /**
     * Get real vertical location (relative location + offset location).
     * 
     * @return The real vertical location
     */
    public double getLocationRealY()
    {
        return location.getLocationY() + offset.getLocationY();
    }

    /**
     * Move camera using specified vector.
     * 
     * @param extrp The extrapolation value.
     * @param vx The horizontal movement.
     * @param vy The vertical movement.
     */
    private void move(double extrp, double vx, double vy)
    {
        // Horizontal move
        // Can scroll only on offset interval
        if (offset.getLocationIntX() <= -intervalHorizontal || offset.getLocationIntX() >= intervalHorizontal)
        {
            location.moveLocation(extrp, vx, 0);
        }
        offset.moveLocation(extrp, vx, 0);

        // Block offset on its limits
        if (offset.getLocationIntX() < -intervalHorizontal)
        {
            offset.setLocationX(-intervalHorizontal);
        }
        if (offset.getLocationIntX() > intervalHorizontal)
        {
            offset.setLocationX(intervalHorizontal);
        }

        // Vertical move
        // Can scroll only on offset interval
        if (offset.getLocationIntY() <= -intervalVertical || offset.getLocationIntX() >= intervalVertical)
        {
            location.moveLocation(extrp, 0, vy);
        }
        offset.moveLocation(extrp, 0, vy);

        // Block offset on its limits
        if (offset.getLocationIntY() < -intervalVertical)
        {
            offset.setLocationY(-intervalVertical);
        }
        if (offset.getLocationIntY() > intervalVertical)
        {
            offset.setLocationY(intervalVertical);
        }
    }

    /*
     * Localizable
     */

    @Override
    public void teleport(double x, double y)
    {
        location.teleport(x, y);
    }

    @Override
    public void teleportX(double x)
    {
        location.teleportX(x);
    }

    @Override
    public void teleportY(double y)
    {
        location.teleportY(y);
    }

    @Override
    public void moveLocation(double extrp, Force force, Force... forces)
    {
        move(extrp, force.getForceHorizontal(), force.getForceVertical());
        for (final Force f : forces)
        {
            move(extrp, f.getForceHorizontal(), f.getForceVertical());
        }
    }

    @Override
    public void moveLocation(double extrp, double vx, double vy)
    {
        move(extrp, vx, vy);
    }

    @Override
    public void setLocation(double x, double y)
    {
        moveLocation(1, x - location.getLocationX(), y - location.getLocationY());
    }

    @Override
    public void setLocationX(double x)
    {
        setLocation(x, getLocationY());
    }

    @Override
    public void setLocationY(double y)
    {
        setLocation(getLocationX(), y);
    }

    @Override
    public void setSize(int width, int height)
    {
        location.setSize(width, height);
    }

    @Override
    public int getLocationIntX()
    {
        return location.getLocationIntX() - getViewX();
    }

    @Override
    public int getLocationIntY()
    {
        return location.getLocationIntY() + getViewY();
    }

    @Override
    public double getLocationX()
    {
        return location.getLocationX() - getViewX();
    }

    @Override
    public double getLocationY()
    {
        return location.getLocationY() + getViewY();
    }

    @Override
    public double getLocationOldX()
    {
        return location.getLocationOldX() - getViewX();
    }

    @Override
    public double getLocationOldY()
    {
        return location.getLocationOldY() + getViewY();
    }

    @Override
    public int getWidth()
    {
        return location.getWidth();
    }

    @Override
    public int getHeight()
    {
        return location.getHeight();
    }

    @Override
    public Line2D getMovement()
    {
        final Line2D m = location.getMovement();
        m.setLine(m.getX1() - getViewX(), m.getY1() + getViewY(), m.getX2() - getViewX(), m.getY2() + getViewY());
        return m;
    }
}
