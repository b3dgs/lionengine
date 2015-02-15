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
package com.b3dgs.lionengine.example.game.strategy.ability.weapon;

import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.example.game.strategy.ability.entity.Entity;
import com.b3dgs.lionengine.example.game.strategy.ability.entity.UnitAttacker;
import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.configurer.Configurer;
import com.b3dgs.lionengine.game.factory.Factory;
import com.b3dgs.lionengine.game.factory.Setup;
import com.b3dgs.lionengine.game.strategy.ability.attacker.WeaponModel;

/**
 * Weapon base implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public abstract class Weapon
        extends WeaponModel<Entity, UnitAttacker>
{
    /**
     * Get an entity configuration file.
     * 
     * @param type The config associated class.
     * @return The media config.
     */
    protected static Media getConfig(Class<? extends Weapon> type)
    {
        return Core.MEDIA.create(FactoryWeapon.WEAPON_DIR, type.getSimpleName() + "."
                + Factory.FILE_DATA_EXTENSION);
    }

    /** Frame. */
    private int frame;

    /**
     * Constructor.
     * 
     * @param setup The setup reference.
     */
    protected Weapon(Setup setup)
    {
        super(setup);

        final Configurer configurer = setup.getConfigurer();
        setAttackFrame(configurer.getInteger("attackFrame"));
        setAttackTimer(configurer.getInteger("attackTimer"));

        final int distMin = configurer.getInteger("min", "distance");
        final int distMax = configurer.getInteger("max", "distance");
        setAttackDistance(distMin, distMax);

        final int dmgMin = configurer.getInteger("min", "damages");
        final int dmgMax = configurer.getInteger("max", "damages");
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

    /*
     * WeaponModel
     */

    @Override
    public void prepare(Services context)
    {
        // Nothing to do
    }
}
