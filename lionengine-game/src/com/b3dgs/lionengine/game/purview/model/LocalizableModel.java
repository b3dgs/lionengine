package com.b3dgs.lionengine.game.purview.model;

import java.awt.geom.Line2D;

import com.b3dgs.lionengine.game.Coord;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.purview.Localizable;

/**
 * Default localizable implementation.
 */
public class LocalizableModel
        implements Localizable
{
    /** Ray cast movement. */
    private final Line2D movement;
    /** Current coordinate. */
    private final Coord current;
    /** Old coordinate. */
    private final Coord old;
    /** Body width. */
    private int width;
    /** Body height. */
    private int height;

    /**
     * Create a localizable, set to <code>(0, 0)</code> by default.
     */
    public LocalizableModel()
    {
        this(0.0, 0.0);
    }

    /**
     * Create a localizable from a coordinate.
     * 
     * @param x The horizontal location.
     * @param y The vertical location.
     */
    public LocalizableModel(double x, double y)
    {
        current = new Coord(x, y);
        old = new Coord(x, y);
        movement = new Line2D.Double(x, y, x, y);
    }

    /**
     * Backup the location by storing the current location in <code>old</code> field.
     */
    private void backupLocation()
    {
        old.set(current.getX(), current.getY());
    }

    /**
     * Update the line between old position and current position.
     */
    private void updateMovement()
    {
        movement.setLine(old.getX(), old.getY(), current.getX(), current.getY());
    }

    /*
     * Localizable
     */

    @Override
    public void moveLocation(double extrp, Force force, Force... forces)
    {
        backupLocation();
        current.translate(force.getForceHorizontal() * extrp, force.getForceVertical() * extrp);
        for (final Force f : forces)
        {
            current.translate(f.getForceHorizontal() * extrp, f.getForceVertical() * extrp);
        }
        updateMovement();
    }

    @Override
    public void moveLocation(double extrp, double vx, double vy)
    {
        backupLocation();
        current.translate(vx * extrp, vy * extrp);
        updateMovement();
    }

    @Override
    public void setLocation(double x, double y)
    {
        current.set(x, y);
        old.set(x, y);
        updateMovement();
    }

    @Override
    public void setLocationX(double x)
    {
        current.setX(x);
        old.setX(x);
        updateMovement();
    }

    @Override
    public void setLocationY(double y)
    {
        current.setY(y);
        old.setY(y);
        updateMovement();
    }

    @Override
    public void setSize(int width, int height)
    {
        this.width = width;
        this.height = height;
    }

    @Override
    public double getLocationX()
    {
        return current.getX();
    }

    @Override
    public double getLocationY()
    {
        return current.getY();
    }

    @Override
    public int getLocationIntX()
    {
        return (int) Math.floor(current.getX());
    }

    @Override
    public int getLocationIntY()
    {
        return (int) Math.floor(current.getY());
    }

    @Override
    public double getLocationOldX()
    {
        return old.getX();
    }

    @Override
    public double getLocationOldY()
    {
        return old.getY();
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
    public Line2D getMovement()
    {
        return movement;
    }
}
