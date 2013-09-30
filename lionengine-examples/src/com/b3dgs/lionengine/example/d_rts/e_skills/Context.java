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
package com.b3dgs.lionengine.example.d_rts.e_skills;

import com.b3dgs.lionengine.example.d_rts.e_skills.entity.FactoryEntity;
import com.b3dgs.lionengine.example.d_rts.e_skills.projectile.FactoryProjectile;
import com.b3dgs.lionengine.example.d_rts.e_skills.skill.FactorySkill;
import com.b3dgs.lionengine.example.d_rts.e_skills.weapon.FactoryWeapon;

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
    /** The handler entity reference. */
    public final HandlerEntity handlerEntity;
    /** The handler projectile reference. */
    public final HandlerProjectile handlerProjectile;
    /** Cursor. */
    public final Cursor cursor;
    /** The desired fps. */
    public final int desiredFps;

    /**
     * Constructor.
     * 
     * @param map The map reference.
     * @param handlerEntity The handler entity reference.
     * @param handlerProjectile The handler arrow reference.
     * @param cursor The cursor reference.
     * @param desiredFps The the desired fps.
     */
    Context(Map map, HandlerEntity handlerEntity, HandlerProjectile handlerProjectile, Cursor cursor, int desiredFps)
    {
        this.map = map;
        this.handlerEntity = handlerEntity;
        this.handlerProjectile = handlerProjectile;
        this.cursor = cursor;
        this.desiredFps = desiredFps;
        factoryProduction = new FactoryProduction();
        factoryEntity = new FactoryEntity();
        factoryProjectile = new FactoryProjectile();
        factoryWeapon = new FactoryWeapon();
        factorySkill = new FactorySkill(handlerEntity, factoryProduction, cursor);
    }

    /**
     * Assign context to factories.
     */
    public void assignContext()
    {
        factoryEntity.setContext(this);
        factoryWeapon.setContext(this);
    }
}
