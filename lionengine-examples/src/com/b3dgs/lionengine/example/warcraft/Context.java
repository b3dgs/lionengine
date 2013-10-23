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
package com.b3dgs.lionengine.example.warcraft;

import com.b3dgs.lionengine.example.warcraft.effect.FactoryEffect;
import com.b3dgs.lionengine.example.warcraft.effect.HandlerEffect;
import com.b3dgs.lionengine.example.warcraft.entity.FactoryEntity;
import com.b3dgs.lionengine.example.warcraft.entity.FactoryProduction;
import com.b3dgs.lionengine.example.warcraft.entity.HandlerEntity;
import com.b3dgs.lionengine.example.warcraft.map.Map;
import com.b3dgs.lionengine.example.warcraft.projectile.FactoryProjectile;
import com.b3dgs.lionengine.example.warcraft.projectile.HandlerProjectile;
import com.b3dgs.lionengine.example.warcraft.skill.FactorySkill;
import com.b3dgs.lionengine.example.warcraft.weapon.FactoryWeapon;
import com.b3dgs.lionengine.game.TimedMessage;
import com.b3dgs.lionengine.game.rts.CameraRts;

/**
 * Context container.
 */
public final class Context
{
    /** The map reference. */
    public final Map map;
    /** The factory reference. */
    public final FactoryEntity factoryEntity;
    /** The factory reference. */
    public final FactoryProjectile factoryProjectile;
    /** The factory skill. */
    public final FactorySkill factorySkill;
    /** The factory production. */
    public final FactoryProduction factoryProduction;
    /** The factory weapon. */
    public final FactoryWeapon factoryWeapon;
    /** The factory effect. */
    public final FactoryEffect factoryEffect;
    /** The handler entity reference. */
    public final HandlerEntity handlerEntity;
    /** The handler projectile reference. */
    public final HandlerProjectile handlerProjectile;
    /** The handler effect reference. */
    public final HandlerEffect handlerEffect;
    /** The timed message reference. */
    public final TimedMessage timedMessage;
    /** Cursor. */
    public final Cursor cursor;
    /** The desired fps. */
    public final int desiredFps;

    /**
     * Constructor.
     * 
     * @param camera The camera reference.
     * @param map The map reference.
     * @param handlerEntity The handler entity reference.
     * @param handlerProjectile The handler arrow reference.
     * @param cursor The cursor reference.
     * @param message The timed message reference.
     * @param desiredFps The the desired fps.
     */
    Context(CameraRts camera, Map map, HandlerEntity handlerEntity, HandlerProjectile handlerProjectile, Cursor cursor,
            TimedMessage message, int desiredFps)
    {
        this.map = map;
        this.handlerEntity = handlerEntity;
        this.handlerProjectile = handlerProjectile;
        this.cursor = cursor;
        this.desiredFps = desiredFps;
        timedMessage = message;
        factoryEntity = new FactoryEntity();
        factoryProduction = new FactoryProduction(factoryEntity);
        factoryProjectile = new FactoryProjectile();
        factoryWeapon = new FactoryWeapon();
        factorySkill = new FactorySkill(factoryProduction, timedMessage);
        factoryEffect = new FactoryEffect();
        handlerEffect = new HandlerEffect(camera);
    }

    /**
     * Assign context to factories.
     */
    public void assignContext()
    {
        factoryEntity.setContext(this);
        factorySkill.setContext(this);
    }
}
