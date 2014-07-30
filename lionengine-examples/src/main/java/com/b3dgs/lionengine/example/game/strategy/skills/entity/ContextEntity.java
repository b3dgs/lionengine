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
package com.b3dgs.lionengine.example.game.strategy.skills.entity;

import com.b3dgs.lionengine.example.game.strategy.skills.map.Map;
import com.b3dgs.lionengine.example.game.strategy.skills.skill.FactorySkill;
import com.b3dgs.lionengine.game.ContextGame;

/**
 * Represents the context related to entities.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class ContextEntity
        implements ContextGame
{
    /** Map reference. */
    public final Map map;
    /** The factory entity. */
    public final FactoryEntity factoryEntity;
    /** The factory skill. */
    public final FactorySkill factorySkill;
    /** Handler entity reference. */
    public final HandlerEntity handlerEntity;
    /** The desired fps. */
    public final int desiredFps;

    /**
     * Constructor.
     * 
     * @param map The map reference.
     * @param factoryEntity The factory entity reference.
     * @param factorySkill The factory skill reference.
     * @param handlerEntity The handler entity reference.
     * @param desiredFps The desired fps.
     */
    public ContextEntity(Map map, FactoryEntity factoryEntity, FactorySkill factorySkill, HandlerEntity handlerEntity,
            int desiredFps)
    {
        this.map = map;
        this.factoryEntity = factoryEntity;
        this.factorySkill = factorySkill;
        this.handlerEntity = handlerEntity;
        this.desiredFps = desiredFps;
    }
}
