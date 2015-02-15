/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.strategy.ability.attacker;

import com.b3dgs.lionengine.anim.AnimState;
import com.b3dgs.lionengine.game.Tiled;
import com.b3dgs.lionengine.game.strategy.entity.EntityStrategy;

/**
 * List of services needed by the attacker.
 * 
 * @param <E> The entity type used.
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public interface AttackerUsedServices<E extends EntityStrategy>
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
}
