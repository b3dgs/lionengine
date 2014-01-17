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
package com.b3dgs.lionengine.example.game.rts.ability.entity;

import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.example.game.rts.ability.map.Map;
import com.b3dgs.lionengine.example.game.rts.ability.projectile.HandlerProjectile;
import com.b3dgs.lionengine.example.game.rts.ability.weapon.FactoryWeapon;
import com.b3dgs.lionengine.game.FactoryObjectGame;
import com.b3dgs.lionengine.game.SetupSurfaceGame;

/**
 * Factory entity implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see com.b3dgs.lionengine.example.game.factory
 */
public final class FactoryEntity
        extends FactoryObjectGame<EntityType, SetupSurfaceGame, Entity>
{
    /** Directory name from our resources directory containing our entities. */
    public static final String ENTITY_PATH = "entities";

    /** Map reference. */
    public final Map map;
    /** The factory weapon. */
    public final FactoryWeapon factoryWeapon;
    /** Handler entity reference. */
    public final HandlerEntity handlerEntity;
    /** The desired fps. */
    public final int desiredFps;

    /**
     * Constructor.
     * 
     * @param map The map reference.
     * @param factoryWeapon The factory weapon reference.
     * @param handlerEntity The handler entity reference.
     * @param handlerProjectile The handler projectile reference.
     * @param desiredFps The desired fps.
     */
    public FactoryEntity(Map map, FactoryWeapon factoryWeapon, HandlerEntity handlerEntity,
            HandlerProjectile handlerProjectile, int desiredFps)
    {
        super(EntityType.class, FactoryEntity.ENTITY_PATH);
        this.map = map;
        this.factoryWeapon = factoryWeapon;
        this.handlerEntity = handlerEntity;
        this.desiredFps = desiredFps;
        load();
    }

    /*
     * FactoryObjectGame
     */

    @Override
    protected SetupEntity createSetup(EntityType type, Media config)
    {
        return new SetupEntity(config, type, map, this, factoryWeapon, handlerEntity, desiredFps);
    }
}
