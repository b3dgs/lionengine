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
package com.b3dgs.lionengine.example.warcraft.weapon;

import com.b3dgs.lionengine.example.warcraft.Context;
import com.b3dgs.lionengine.example.warcraft.entity.Attacker;
import com.b3dgs.lionengine.example.warcraft.entity.Entity;
import com.b3dgs.lionengine.game.SetupGame;

/**
 * Axe weapon implementation.
 */
public final class Axe
        extends Weapon
{
    /**
     * Constructor.
     * 
     * @param setup The setup reference.
     * @param user The user reference.
     * @param context The context reference.
     */
    public Axe(SetupGame setup, Attacker user, Context context)
    {
        super(WeaponType.AXE, setup, user, context);
    }

    /*
     * Weapon
     */

    @Override
    public void notifyAttackEnded(int damages, Entity target)
    {
        target.decreaseLife(damages, user);
    }
}
