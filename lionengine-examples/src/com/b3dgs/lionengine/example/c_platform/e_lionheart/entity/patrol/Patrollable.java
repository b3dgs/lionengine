package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.patrol;

import com.b3dgs.lionengine.game.purview.Localizable;
import com.b3dgs.lionengine.game.purview.Mirrorable;

/**
 * Represents something that can patrol.
 */
public interface Patrollable
        extends Localizable, Mirrorable, Patroller
{
    /**
     * Set the movement force.
     * 
     * @param fh The horizontal force.
     * @param fv The vertical force.
     */
    void setMovementForce(double fh, double fv);
    
    /**
     * Set the maximum movement speed.
     * @param speed The maximum movement speed.
     */
    void setMovementSpeedMax(double speed);

    /**
     * Get the max movement speed.
     * 
     * @return The max movement speed.
     */
    double getMovementSpeedMax();

    /**
     * Get the horizontal movement force.
     * 
     * @return The horizontal movement force.
     */
    double getForceHorizontal();
}
