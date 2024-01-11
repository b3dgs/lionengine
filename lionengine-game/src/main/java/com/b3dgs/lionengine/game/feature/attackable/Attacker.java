/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import java.util.function.Predicate;
import java.util.function.ToDoubleBiFunction;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Listenable;
import com.b3dgs.lionengine.Range;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.RoutineUpdate;
import com.b3dgs.lionengine.game.feature.Transformable;

/**
 * Represents something that can attack something at a defined rate, distance and damages.
 */
@FeatureInterface
public interface Attacker extends RoutineUpdate, Listenable<AttackerListener>
{
    /**
     * Define a target to attack.
     * 
     * @param target The target to attack (can be <code>null</code> for not target).
     */
    void attack(Transformable target);

    /**
     * Stop the current attack.
     */
    void stopAttack();

    /**
     * Set attack checker.
     * 
     * @param checker The checker function allowing attack (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    void setAttackChecker(Predicate<Transformable> checker);

    /**
     * Set attack distance computer.
     * 
     * @param distance The attack distance computer reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    void setAttackDistanceComputer(ToDoubleBiFunction<Transformable, Transformable> distance);

    /**
     * Set attack pause time between two attacks.
     * 
     * @param tick The attack pause time in tick (must be positive).
     * @throws LionEngineException If invalid argument.
     * @see Tick
     */
    void setAttackDelay(int tick);

    /**
     * Set the frame number which represents the attack action.
     * 
     * @param frame The attack frame number (must be strictly positive).
     * @throws LionEngineException If invalid argument.
     */
    void setAttackFrame(int frame);

    /**
     * Set attack distance between source and target. Attack will be possible inside this range, else it will need to
     * reach target in order to perform the attack.
     * 
     * @param range The attack range in tile (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    void setAttackDistance(Range range);

    /**
     * Set attack damages.
     * 
     * @param damages The attack damages (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    void setAttackDamages(Range damages);

    /**
     * Get a random attack damages (between min and max).
     * 
     * @return The attack damages.
     */
    int getAttackDamages();

    /**
     * Check is attacking state.
     * 
     * @return <code>true</code> if currently attacking, <code>false</code> else.
     */
    boolean isAttacking();

    /**
     * Get the current target.
     * 
     * @return The target reference (<code>null</code> if not defined).
     */
    Transformable getTarget();
}
