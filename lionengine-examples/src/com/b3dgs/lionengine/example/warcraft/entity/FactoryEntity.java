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
package com.b3dgs.lionengine.example.warcraft.entity;

import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.example.warcraft.AppWarcraft;
import com.b3dgs.lionengine.example.warcraft.effect.FactoryEffect;
import com.b3dgs.lionengine.example.warcraft.effect.HandlerEffect;
import com.b3dgs.lionengine.example.warcraft.map.Map;
import com.b3dgs.lionengine.example.warcraft.projectile.HandlerProjectile;
import com.b3dgs.lionengine.example.warcraft.skill.FactorySkill;
import com.b3dgs.lionengine.example.warcraft.weapon.FactoryWeapon;
import com.b3dgs.lionengine.game.FactoryObjectGame;
import com.b3dgs.lionengine.game.TimedMessage;

/**
 * Factory entity implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class FactoryEntity
        extends FactoryObjectGame<EntityType, SetupEntity, Entity>
{
    /** Map reference. */
    public final Map map;
    /** Timed message. */
    public final TimedMessage message;
    /** The factory skill. */
    public final FactorySkill factorySkill;
    /** The factory weapon. */
    public final FactoryWeapon factoryWeapon;
    /** The factory effect. */
    public final FactoryEffect factoryEffect;
    /** Handler entity reference. */
    public final HandlerEntity handlerEntity;
    /** Handler entity reference. */
    public final HandlerEffect handlerEffect;
    /** The desired fps. */
    public final int desiredFps;

    /**
     * Constructor.
     * 
     * @param map The map reference.
     * @param message The timed message reference.
     * @param factoryEffect The factory effect reference.
     * @param factorySkill The factory skill reference.
     * @param factoryWeapon The factory weapon reference.
     * @param handlerEntity The handler entity reference.
     * @param handlerEffect The handler effect reference.
     * @param handlerProjectile The handler projectile reference.
     * @param desiredFps The desired fps.
     */
    public FactoryEntity(Map map, TimedMessage message, FactoryEffect factoryEffect, FactorySkill factorySkill,
            FactoryWeapon factoryWeapon, HandlerEntity handlerEntity, HandlerEffect handlerEffect,
            HandlerProjectile handlerProjectile, int desiredFps)
    {
        super(EntityType.class, AppWarcraft.ENTITIES_DIR);
        this.map = map;
        this.message = message;
        this.factoryEffect = factoryEffect;
        this.factorySkill = factorySkill;
        this.factoryWeapon = factoryWeapon;
        this.handlerEntity = handlerEntity;
        this.handlerEffect = handlerEffect;
        this.desiredFps = desiredFps;
        load();
    }

    /*
     * FactoryObjectGame
     */

    @Override
    protected SetupEntity createSetup(EntityType type, Media config)
    {
        return new SetupEntity(config, type, map, message, this, factoryEffect, factorySkill, factoryWeapon,
                handlerEntity, handlerEffect, desiredFps);
    }
}
