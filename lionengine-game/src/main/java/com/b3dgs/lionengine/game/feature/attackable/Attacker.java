/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.attackable;

import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.game.Feature;
import com.b3dgs.lionengine.game.feature.Transformable;

/**
 * List of services provided by a weapon.
 */
public interface Attacker extends Feature, Updatable
{
    /**
     * Add an attacker listener.
     * 
     * @param listener The attacker listener to add.
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
     * @param time The attack pause time.
     */
    void setAttackTimer(int time);

    /**
     * Represents the frame number (inside animation) which design the attack action.
     * <p>
     * Example: for a footman, the frame number when the sword is going to hurt somebody (between 1-last).
     * </p>
     * 
     * @param frame The frame attack number.
     */
    void setAttackFrame(int frame);

    /**
     * Set attack distance between entity and target.
     * 
     * @param min The minimum attack distance (in tile).
     * @param max The maximum attack distance (in tile).
     */
    void setAttackDistance(int min, int max);

    /**
     * Set attack damages between entity and target.
     * 
     * @param min The minimum attack damages.
     * @param max The maximum attack damages.
     */
    void setAttackDamages(int min, int max);

    /**
     * Get a random attack damages (between min and max).
     * 
     * @return The attack damages.
     */
    int getAttackDamages();

    /**
     * Check weapon is attacking.
     * 
     * @return <code>true</code> if currently attacking, <code>false</code> else.
     */
    boolean isAttacking();

    /**
     * Get the target entity.
     * 
     * @return The target entity.
     */
    Transformable getTarget();
}
