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
package com.b3dgs.lionengine.example.tyrian.entity.scenery;

import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.example.tyrian.effect.FactoryEffect;
import com.b3dgs.lionengine.example.tyrian.effect.HandlerEffect;
import com.b3dgs.lionengine.example.tyrian.entity.Entity;
import com.b3dgs.lionengine.example.tyrian.entity.SetupEntity;
import com.b3dgs.lionengine.game.FactoryObjectGame;

/**
 * Factory entity scenery.
 */
public final class FactoryEntityScenery
        extends FactoryObjectGame<EntitySceneryType, SetupEntity, Entity>
{
    /** Factory effect. */
    final FactoryEffect factoryEffect;
    /** Handler effect. */
    final HandlerEffect handlerEffect;

    /**
     * Constructor.
     * 
     * @param factoryEffect The effect factory reference.
     * @param handlerEffect The handler effect reference.
     */
    public FactoryEntityScenery(FactoryEffect factoryEffect, HandlerEffect handlerEffect)
    {
        super(EntitySceneryType.class, Media.getPath("entities", "scenery"));
        this.factoryEffect = factoryEffect;
        this.handlerEffect = handlerEffect;
        load();
    }

    /*
     * FactoryObjectGame
     */

    @Override
    protected SetupEntity createSetup(EntitySceneryType key, Media config)
    {
        return new SetupEntity(config, factoryEffect, handlerEffect);
    }
}
