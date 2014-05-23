/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.example.game.strategy.ability.weapon;

import com.b3dgs.lionengine.example.game.strategy.ability.entity.Entity;
import com.b3dgs.lionengine.example.game.strategy.ability.entity.UnitAttacker;
import com.b3dgs.lionengine.game.purview.Configurable;
import com.b3dgs.lionengine.game.strategy.ability.attacker.WeaponModel;

/**
 * Weapon base implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public abstract class Weapon
        extends WeaponModel<Entity, UnitAttacker>
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

        final Configurable configurable = setup.getConfigurable();
        setAttackFrame(configurable.getInteger("attackFrame"));
        setAttackTimer(configurable.getInteger("attackTimer"));

        final int distMin = configurable.getInteger("min", "distance");
        final int distMax = configurable.getInteger("max", "distance");
        setAttackDistance(distMin, distMax);

        final int dmgMin = configurable.getInteger("min", "damages");
        final int dmgMax = configurable.getInteger("max", "damages");
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
     * Get the frame.
     * 
     * @return The frame.
     */
    public int getFrame()
    {
        return frame;
    }
}
