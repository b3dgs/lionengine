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

import com.b3dgs.lionengine.core.ImageBuffer;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.UtilityImage;
import com.b3dgs.lionengine.example.warcraft.AppWarcraft;
import com.b3dgs.lionengine.example.warcraft.RaceType;
import com.b3dgs.lionengine.example.warcraft.effect.FactoryEffect;
import com.b3dgs.lionengine.example.warcraft.effect.HandlerEffect;
import com.b3dgs.lionengine.example.warcraft.map.Map;
import com.b3dgs.lionengine.example.warcraft.skill.FactorySkill;
import com.b3dgs.lionengine.example.warcraft.weapon.FactoryWeapon;
import com.b3dgs.lionengine.game.SetupSurfaceGame;
import com.b3dgs.lionengine.game.TimedMessage;

/**
 * Setup entity implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class SetupEntity
        extends SetupSurfaceGame
{
    /** Type. */
    public final EntityType type;
    /** Corpse. */
    public final ImageBuffer corpse;
    /** Map. */
    public final Map map;
    /** Timed message. */
    public final TimedMessage message;
    /** Factory entity. */
    public final FactoryEntity factoryEntity;
    /** Factory effect. */
    public final FactoryEffect factoryEffect;
    /** Factory skill. */
    public final FactorySkill factorySkill;
    /** Factory weapon. */
    public final FactoryWeapon factoryWeapon;
    /** Handler entity. */
    public final HandlerEntity handlerEntity;
    /** handler effect. */
    public final HandlerEffect handlerEffect;
    /** Desired fps. */
    public final int fps;

    /**
     * Constructor.
     * 
     * @param config The config file.
     * @param type The entity type.
     * @param map The map reference.
     * @param message The timed message reference.
     * @param factoryEntity The factory entity reference.
     * @param factoryEffect The factory effect reference.
     * @param factorySkill The factory skill reference.
     * @param factoryWeapon The factory weapon reference.
     * @param handlerEntity The handler entity reference.
     * @param handlerEffect The handler effect reference.
     * @param fps The desired fps.
     */
    public SetupEntity(Media config, EntityType type, Map map, TimedMessage message, FactoryEntity factoryEntity,
            FactoryEffect factoryEffect, FactorySkill factorySkill, FactoryWeapon factoryWeapon,
            HandlerEntity handlerEntity, HandlerEffect handlerEffect, int fps)
    {
        super(config);
        this.type = type;
        this.map = map;
        this.message = message;
        this.factoryEntity = factoryEntity;
        this.factoryEffect = factoryEffect;
        this.factorySkill = factorySkill;
        this.factoryWeapon = factoryWeapon;
        this.handlerEntity = handlerEntity;
        this.handlerEffect = handlerEffect;
        this.fps = fps;
        if (type.race == RaceType.NEUTRAL)
        {
            corpse = null;
        }
        else
        {
            corpse = UtilityImage.getImageBuffer(
                    Media.get(AppWarcraft.EFFECTS_DIR, "corpse_" + type.race.getPathName() + ".png"), false);
        }
    }
}
