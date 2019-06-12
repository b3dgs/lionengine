/*
 * Copyright (C) 2013-2019 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.lionengine.game.feature.attackable;

import com.b3dgs.lionengine.game.feature.Transformable;

/**
 * List of events listened from an attacker.
 */
public interface AttackerListener
{
    /**
     * Notify while attacker is reaching target.
     * 
     * @param target The target to reach.
     */
    void notifyReachingTarget(Transformable target);

    /**
     * Notify when attacker is going to attack.
     * 
     * @param target The target reference.
     */
    void notifyAttackStarted(Transformable target);

    /**
     * Notify when attacker has done his attack.
     * 
     * @param target The target reference.
     * @param damages The final damages.
     */
    void notifyAttackEnded(int damages, Transformable target);

    /**
     * Notify when attacker has played his attack completely.
     */
    void notifyAttackAnimEnded();

    /**
     * Notify while attacker is waiting for next attack.
     */
    void notifyPreparingAttack();
}
