package com.b3dgs.lionengine.example.d_rts.f_warcraft.entity;

import com.b3dgs.lionengine.game.Orientation;
import com.b3dgs.lionengine.game.rts.ability.attacker.AttackerUsedServices;

/**
 * Attacker interface.
 */
public interface Attacker
        extends AttackerUsedServices<Entity>
{
    /**
     * Get current orientation.
     * 
     * @return The current orientation.
     */
    Orientation getOrientation();
}
