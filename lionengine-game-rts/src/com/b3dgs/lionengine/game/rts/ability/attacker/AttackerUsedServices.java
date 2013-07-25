package com.b3dgs.lionengine.game.rts.ability.attacker;

import com.b3dgs.lionengine.anim.AnimState;
import com.b3dgs.lionengine.game.Tiled;
import com.b3dgs.lionengine.game.rts.EntityRts;

/**
 * List of services needed by the attacker.
 * 
 * @param <E> The entity type used.
 */
public interface AttackerUsedServices<E extends EntityRts>
        extends AttackerListener<E>
{
    /**
     * Check if the attacker can attack (called when attack is possible).
     * 
     * @return <code>true</code> if can start the attack, <code>false</code> else.
     */
    boolean canAttack();

    /**
     * Get the distance in tile between attacker and target.
     * 
     * @param target The target reference.
     * @param fromCenter <code>true</code> to get distance from center only, <code>false</code> from the global area.
     * @return The distance in tile.
     */
    int getDistanceInTile(Tiled target, boolean fromCenter);

    /**
     * Get the current playing frame.
     * 
     * @return The current frame.
     */
    int getFrame();

    /**
     * Get the current animation state.
     * 
     * @return The animation state.
     */
    AnimState getAnimState();

    /**
     * Get the current rounded horizontal location.
     * 
     * @return The current rounded horizontal location.
     */
    int getLocationIntX();

    /**
     * Get the current rounded vertical location.
     * 
     * @return The current rounded vertical location.
     */
    int getLocationIntY();

    /**
     * Get the width.
     * 
     * @return The width.
     */
    int getWidth();

    /**
     * Get the height.
     * 
     * @return The height.
     */
    int getHeight();
}
