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

import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.example.game.strategy.skills.map.Map;
import com.b3dgs.lionengine.example.game.strategy.skills.skill.FactorySkill;
import com.b3dgs.lionengine.game.FactoryObjectGame;

/**
 * Factory entity implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class FactoryEntity
        extends FactoryObjectGame<SetupEntity, Entity>
{
    /** Map reference. */
    public final Map map;
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
     * @param factorySkill The factory skill reference.
     * @param handlerEntity The handler entity reference.
     * @param desiredFps The desired fps.
     */
    public FactoryEntity(Map map, FactorySkill factorySkill, HandlerEntity handlerEntity, int desiredFps)
    {
        super("entity");
        this.map = map;
        this.factorySkill = factorySkill;
        this.handlerEntity = handlerEntity;
        this.desiredFps = desiredFps;
    }

    /*
     * FactoryObjectGame
     */

    @Override
    protected SetupEntity createSetup(Class<? extends Entity> type, Media config)
    {
        return new SetupEntity(config, type, map, this, factorySkill, handlerEntity, desiredFps);
    }
}
