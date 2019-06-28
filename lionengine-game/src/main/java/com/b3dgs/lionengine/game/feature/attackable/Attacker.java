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

import com.b3dgs.lionengine.Range;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.game.Feature;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.Transformable;

/**
 * Represents something that can attack something at a defined rate, distance and damages.
 */
@FeatureInterface
public interface Attacker extends Feature, Updatable
{
    /**
     * Add a listener.
     * 
     * @param listener The listener to add.
     */
    void addListener(AttackerListener listener);

    /**
     * Define a target to attack.
     * 
     * @param target The target to attack.
     */
    void attack(Transformable target);

    /**
     * Stop the current attack.
     */
    void stopAttack();

    /**
     * Set attack pause time between two attacks.
     * 
     * @param tick The attack pause time in tick.
     * @see Tick
     */
    void setAttackTimer(int tick);

    /**
     * Set the frame number (inside animation) which represents the attack action.
     * <p>
     * Example: for a soldier, the frame number when the sword is going to hurt somebody.
     * </p>
     * 
     * @param frame The attack frame number.
     */
    void setAttackFrame(int frame);

    /**
     * Set attack distance between source and target. Attack will be possible inside this range, else it will need to
     * reach target in order to perform the attack.
     * 
     * @param range The attack range in tile.
     */
    void setAttackDistance(Range range);

    /**
     * Set attack damages.
     * 
     * @param damages The attack damages.
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
     * @return The target reference.
     */
    Transformable getTarget();
}
