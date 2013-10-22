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
package com.b3dgs.lionengine.example.game.rts.skills;

import com.b3dgs.lionengine.example.game.rts.skills.entity.FactoryEntity;
import com.b3dgs.lionengine.example.game.rts.skills.entity.FactoryProduction;
import com.b3dgs.lionengine.example.game.rts.skills.entity.HandlerEntity;
import com.b3dgs.lionengine.example.game.rts.skills.map.Map;
import com.b3dgs.lionengine.example.game.rts.skills.skill.FactorySkill;

/**
 * Context container.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class Context
{
    /** The map reference. */
    public final Map map;
    /** The factory reference. */
    public final FactoryEntity factoryEntity;
    /** The factory skill. */
    public final FactorySkill factorySkill;
    /** The factory production. */
    public final FactoryProduction factoryProduction;
    /** The handler entity reference. */
    public final HandlerEntity handlerEntity;
    /** Cursor. */
    public final Cursor cursor;
    /** The desired fps. */
    public final int desiredFps;

    /**
     * Constructor.
     * 
     * @param map The map reference.
     * @param handlerEntity The handler entity reference.
     * @param cursor The cursor reference.
     * @param desiredFps The the desired fps.
     */
    Context(Map map, HandlerEntity handlerEntity, Cursor cursor, int desiredFps)
    {
        this.map = map;
        this.handlerEntity = handlerEntity;
        this.cursor = cursor;
        this.desiredFps = desiredFps;
        factoryEntity = new FactoryEntity();
        factoryProduction = new FactoryProduction(factoryEntity);
        factorySkill = new FactorySkill(factoryProduction, cursor);
    }

    /**
     * Assign context to factories.
     */
    public void assignContext()
    {
        factoryEntity.setContext(this);
    }
}
