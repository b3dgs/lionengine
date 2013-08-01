package com.b3dgs.lionengine.game.purview;

import com.b3dgs.lionengine.game.Force;

/**
 * Represents something designed to receive a gravitational force.
 */
public interface Body
{
    /**
     * Update gravity calculation.
     * 
     * @param extrp The extrapolation value.
     * @param desiredFps The desired fps (usually 60)
     * @param forces The list of forces.
     */
    void updateGravity(double extrp, int desiredFps, Force... forces);

    /**
     * Reset gravity force (usually when hit the ground).
     */
    void resetGravity();

    /**
     * True to invert Y axis.
     * 
     * @param state The state.
     */
    void invertAxisY(boolean state);

    /**
     * Set the maximum gravity value.
     * 
     * @param max The maximum gravity value.
     */
    void setGravityMax(double max);

    /**
     * Set body mass.
     * 
     * @param mass The body mass.
     */
    void setMass(double mass);

    /**
     * Get body mass.
     * 
     * @return The body mass.
     */
    double getMass();

    /**
     * Get body weight.
     * 
     * @return The body weight.
     */
    double getWeight();
}
