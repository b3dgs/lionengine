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

import com.b3dgs.lionengine.example.warcraft.entity.Attacker;
import com.b3dgs.lionengine.example.warcraft.entity.Entity;
import com.b3dgs.lionengine.game.rts.ability.attacker.WeaponModel;

/**
 * Weapon base implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public abstract class Weapon
        extends WeaponModel<Entity, Attacker>
{
    /** Frame. */
    private int frame;

    /**
     * Constructor.
     * 
     * @param setup The setup reference.
     */
    protected Weapon(SetupWeapon setup)
    {
        super(setup);

        setAttackFrame(getDataInteger("attackFrame"));
        setAttackTimer(getDataInteger("attackTimer"));

        final int distMin = getDataInteger("min", "distance");
        final int distMax = getDataInteger("max", "distance");
        setAttackDistance(distMin, distMax);

        final int dmgMin = getDataInteger("min", "damages");
        final int dmgMax = getDataInteger("max", "damages");
        setAttackDamages(dmgMin, dmgMax);
    }

    /**
     * Set the weapon frame.
     * 
     * @param frame The weapon frame.
     */
    public void setFrame(int frame)
    {
        this.frame = frame;
    }

    /**
     * Get the frame number.
     * 
     * @return The frame number.
     */
    public int getFrame()
    {
        return frame;
    }
}
