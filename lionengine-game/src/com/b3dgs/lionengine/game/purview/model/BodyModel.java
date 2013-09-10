package com.b3dgs.lionengine.game.purview.model;

import java.awt.geom.Line2D;

import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.purview.Body;
import com.b3dgs.lionengine.game.purview.Localizable;

/**
 * Default body implementation.
 */
public class BodyModel
        implements Body, Localizable
{
    /** Gravity of earth (in m/s). */
    private static final double GRAVITY = 9.80665;
    /** Body location. */
    protected final Localizable location;
    /** Body force. */
    private final Force force;
    /** Maximum gravity value. */
    private final Force gravityMax;
    /** Body mass. */
    private double mass;
    /** Invert axis. */
    private int invertY;

    /**
     * Create a new body.
     */
    public BodyModel()
    {
        this(null);
    }

    /**
     * Create a new body from an existing one.
     * 
     * @param body The body to copy
     */
    public BodyModel(BodyModel body)
    {
        if (body == null)
        {
            location = new LocalizableModel();
            force = new Force();
        }
        else
        {
            location = new LocalizableModel(body.getLocationX(), body.getLocationY());
            force = new Force(body.force);
        }
        gravityMax = new Force();
        invertY = -1;
    }

    /*
     * Body model
     */

    @Override
    public void updateGravity(double extrp, int desiredFps, Force... forces)
    {
        force.addForce(extrp, 0.0, getWeight() * invertY / desiredFps);
        location.moveLocation(extrp, force, forces);
    }

    @Override
    public void resetGravity()
    {
        force.setForce(Force.ZERO);
    }

    @Override
    public void invertAxisY(boolean state)
    {
        invertY = state ? -1 : 1;
    }

    @Override
    public void setGravityMax(double max)
    {
        gravityMax.setForce(0.0, -max);
        force.setForceMaximum(Force.ZERO);
        force.setForceMinimum(gravityMax);
    }

    @Override
    public void setMass(double mass)
    {
        this.mass = mass;
    }

    @Override
    public double getMass()
    {
        return mass;
    }

    @Override
    public double getWeight()
    {
        return mass * BodyModel.GRAVITY;
    }

    /*
     * Localizable model
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
        location.moveLocation(extrp, force, forces);
    }

    @Override
    public void moveLocation(double extrp, double vx, double vy)
    {
        location.moveLocation(extrp, vx, vy);
    }

    @Override
    public void setLocation(double x, double y)
    {
        location.setLocation(x, y);
    }

    @Override
    public void setLocationX(double x)
    {
        location.setLocationX(x);
    }

    @Override
    public void setLocationY(double y)
    {
        location.setLocationY(y);
    }

    @Override
    public void setSize(int width, int height)
    {
        location.setSize(width, height);
    }

    @Override
    public double getLocationX()
    {
        return location.getLocationX();
    }

    @Override
    public double getLocationY()
    {
        return location.getLocationY();
    }

    @Override
    public int getLocationIntX()
    {
        return location.getLocationIntX();
    }

    @Override
    public int getLocationIntY()
    {
        return location.getLocationIntY();
    }

    @Override
    public double getLocationOldX()
    {
        return location.getLocationOldX();
    }

    @Override
    public double getLocationOldY()
    {
        return location.getLocationOldY();
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
        return location.getMovement();
    }
}
