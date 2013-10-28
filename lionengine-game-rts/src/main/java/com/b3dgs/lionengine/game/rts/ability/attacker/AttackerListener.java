/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.b3dgs.lionengine.game.rts.ability.attacker;

import com.b3dgs.lionengine.game.rts.entity.EntityRts;

/**
 * List of events listened from an attacker.
 * 
 * @param <E> The entity type used.
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public interface AttackerListener<E extends EntityRts>
{
    /**
     * Notify while attacker is reaching target.
     * 
     * @param target The target to reach.
     */
    void notifyReachingTarget(E target);

    /**
     * Notify when attacker is going to attack.
     * 
     * @param target The target reference.
     */
    void notifyAttackStarted(E target);

    /**
     * Notify when attacker has done his attack.
     * 
     * @param target The target reference.
     * @param damages The final damages.
     */
    void notifyAttackEnded(int damages, E target);

    /**
     * Notify when attacker has played his attack completely.
     */
    void notifyAttackAnimEnded();

    /**
     * Notify while attacker is waiting for next attack.
     */
    void notifyPreparingAttack();

    /**
     * Notify when attacker lost its target.
     * 
     * @param target The target lost.
     */
    void notifyTargetLost(E target);
}
