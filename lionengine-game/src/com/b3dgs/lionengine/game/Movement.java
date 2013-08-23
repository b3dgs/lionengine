package com.b3dgs.lionengine.game;

/**
 * Represents a movement based on a current force and a force to reach.
 */
public class Movement
{
    /** The current force. */
    private final Force current;
    /** The force to reach. */
    private final Force destination;
    /** The reach speed. */
    private double velocity;
    /** The sensibility. */
    private double sensibility;
    /** Old horizontal force. */
    private double forceOldH;

    /**
     * Constructor.
     */
    public Movement()
    {
        current = new Force();
        destination = new Force();
    }

    /**
     * Update the movement by updating the force calculation.
     * 
     * @param extrp The extrapolation value.
     */
    public void update(double extrp)
    {
        forceOldH = current.getForceHorizontal();
        current.reachForce(extrp, destination, velocity, sensibility);
    }

    /**
     * Reset the force to zero.
     */
    public void reset()
    {
        destination.setForce(Force.ZERO);
        current.setForce(Force.ZERO);
    }

    /**
     * Set the force to reach.
     * 
     * @param force The force to reach.
     */
    public void setForceToReach(Force force)
    {
        destination.setForce(force);
    }

    /**
     * Set the force to reach.
     * 
     * @param fh The horizontal force.
     * @param fv The vertical force.
     */
    public void setForceToReach(double fh, double fv)
    {
        destination.setForce(fh, fv);
    }

    /**
     * Set the movement velocity.
     * 
     * @param velocity The movement velocity.
     */
    public void setVelocity(double velocity)
    {
        this.velocity = velocity;
    }

    /**
     * Set the sensibility value.
     * 
     * @param sensibility The sensibility value.
     */
    public void setSensibility(double sensibility)
    {
        this.sensibility = sensibility;
    }

    /**
     * Get the current force.
     * 
     * @return The current force.
     */
    public Force getForce()
    {
        return current;
    }

    /**
     * Check if movement is horizontally decreasing.
     * 
     * @return <code>true</code> if horizontally decreasing, <code>false</code> else;
     */
    public boolean isDecreasingHorizontal()
    {
        return Math.abs(forceOldH) > Math.abs(current.getForceHorizontal());
    }

    /**
     * Check if movement is horizontally decreasing.
     * 
     * @return <code>true</code> if horizontally decreasing, <code>false</code> else;
     */
    public boolean isIncreasingHorizontal()
    {
        return Math.abs(forceOldH) < Math.abs(current.getForceHorizontal());
    }
}
