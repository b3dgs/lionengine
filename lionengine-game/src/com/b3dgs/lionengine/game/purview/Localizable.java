package com.b3dgs.lionengine.game.purview;

import java.awt.geom.Line2D;

import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.Surface;

/**
 * Represents something that can be localizable, using a coordinate. It keeps old location to perform a ray cast
 * calculation after a movement, between the last position and the current.
 */
public interface Localizable
        extends Surface
{
    /**
     * Teleport to a new location. Old location is not stored. Movement is equal to 0.
     * 
     * @param x The new horizontal location.
     * @param y The new vertical location.
     */
    void teleport(double x, double y);

    /**
     * Teleport to a new horizontal location. Old location is not stored.
     * 
     * @param x The new horizontal location.
     */
    void teleportX(double x);

    /**
     * Teleport to a new vertical location. Old location is not stored.
     * 
     * @param y The new vertical location.
     */
    void teleportY(double y);

    /**
     * Move location using different forces. Old location is stored before moving and the movement is updated after
     * calculation.
     * 
     * @param extrp The extrapolation value.
     * @param force The primary force.
     * @param forces The other forces.
     */
    void moveLocation(double extrp, Force force, Force... forces);

    /**
     * Move location using a simple force. Old location is stored before moving and the movement is updated after
     * calculation.
     * 
     * @param extrp The extrapolation value.
     * @param vx The horizontal force.
     * @param vy The vertical force.
     */
    void moveLocation(double extrp, double vx, double vy);

    /**
     * Set location instantly. Old location is stored before moving and the movement is updated after calculation.
     * 
     * @param x The new horizontal location.
     * @param y The new vertical location.
     */
    void setLocation(double x, double y);

    /**
     * Set horizontal location. Old location is stored before moving and the movement is updated after calculation.
     * 
     * @param x The new horizontal location.
     */
    void setLocationX(double x);

    /**
     * Set vertical location. Old location is stored before moving and the movement is updated after calculation.
     * 
     * @param y The new vertical location.
     */
    void setLocationY(double y);

    /**
     * Set size.
     * 
     * @param width The width.
     * @param height The height.
     */
    void setSize(int width, int height);

    /**
     * Get the current horizontal location.
     * 
     * @return The current horizontal location.
     */
    double getLocationX();

    /**
     * Get the current vertical location.
     * 
     * @return The current vertical location.
     */
    double getLocationY();

    /**
     * Get the old horizontal location.
     * 
     * @return The old horizontal location.
     */
    double getLocationOldX();

    /**
     * Get the old vertical location.
     * 
     * @return The old vertical location.
     */
    double getLocationOldY();

    /**
     * Get the movement from the old to the current location as a line.
     * 
     * @return The movement line.
     */
    Line2D getMovement();
}
